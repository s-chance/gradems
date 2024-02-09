-- 用户
create table users
(
    id       serial primary key,
    username varchar(255) unique not null,
    password varchar(255) not null
);
insert into users (username, password) values ('admin', 'admin');
insert into users (username, password) values ('user', 'user');
-- 角色
create table roles
(
    id   serial primary key,
    name varchar(255) not null
);
insert into roles (name) values ('admin');
insert into roles (name) values ('user');
-- 用户角色
create table user_role
(
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id),
    constraint fk_user_id foreign key (user_id) references users (id),
    constraint fk_role_id foreign key (role_id) references roles (id)
);
insert into user_role (user_id, role_id) values (1, 1);
insert into user_role (user_id, role_id) values (2, 2);
-- 系
create table department
(
    d_id   varchar(4) primary key,
    d_name varchar(20) not null
);
-- 课程
create table course
(
    c_id   varchar(6) primary key,
    c_name varchar(20) not null,
    c_hour int
);
-- 学生
create table student
(
    s_id       varchar(10) primary key,
    s_name     varchar(20) not null,
    d_id       varchar(4),
    start_date date,
    s_hour     int,
    constraint fk_d_id foreign key (d_id) references department (d_id)
);
-- 成绩
create table report
(
    s_id  varchar(10) not null,
    c_id  varchar(6)  not null,
    grade int,
    primary key (s_id, c_id),
    constraint student_report foreign key (s_id) references student (s_id),
    constraint course_report foreign key (c_id) references course (c_id)
);

-- 创建存储过程
create or replace procedure delete_graduate(end_date varchar(10), min_credit integer)
    language plpgsql
as
$$
declare
    stu_id varchar(10);
    c cursor is select s_id
                from student
                where start_date < to_date(end_date, 'yyyy-mm-dd')
                  and s_hour > min_credit;
begin
    open c;
    loop
        fetch c into stu_id;
        exit when not found;
        delete from report where s_id = stu_id;
        delete from student where s_id = stu_id;
    end loop;
    close c;
end
$$;

-- 创建触发器
create or replace function report_update_trigger() returns trigger as
$$
declare
BEGIN
    update student set s_hour = s_hour + (select c_hour from course where c_id = new.c_id) where s_id = new.s_id;
    return new;
end;
$$ language plpgsql;

create trigger report_update_trigger
    after insert
    on report
    for each row
execute procedure report_update_trigger();

drop trigger report_update_trigger on report;

create or replace function report_delete_trigger() returns trigger as
$$
declare
begin
    update student
    set s_hour = s_hour - (select c_hour from course where c_id = old.c_id)
    where old.grade >= 60
      and s_id = old.s_id;
    return old;
end;
$$ language plpgsql;

create trigger report_delete_trigger
    after delete
    on report
    for each row
execute procedure report_delete_trigger();

drop trigger report_delete_trigger on report;

-- 测试数据
insert into department values ('01', '计算机系');
insert into department values ('02', '电子系');
insert into department values ('03', '机械系');

insert into course values ('01-01', '高等数学', 64);
insert into course values ('01-02', '线性代数', 48);
insert into course values ('01-03', '概率论', 48);
insert into course values ('01-04', '离散数学', 48);

insert into student values ('2012001', '张三', '01', '2012-09-01', 0);
insert into student values ('2012002', '李四', '01', '2012-09-01', 0);
insert into student values ('2012003', '王五', '02', '2012-09-01', 0);
insert into student values ('2012004', '赵六', '03', '2012-09-01', 130);

insert into report values ('2012001', '01-01', 80);
insert into report values ('2012001', '01-02', 75);
insert into report values ('2012001', '01-03', 85);
insert into report values ('2012001', '01-04', 90);
insert into report values ('2012002', '01-01', 70);
insert into report values ('2012002', '01-02', 65);
insert into report values ('2012002', '01-03', 75);
insert into report values ('2012002', '01-04', 80);
insert into report values ('2012003', '01-01', 60);
insert into report values ('2012003', '01-02', 55);
insert into report values ('2012003', '01-03', 65);
insert into report values ('2012003', '01-04', 70);
insert into report values ('2012004', '01-01', 80);
insert into report values ('2012004', '01-02', 75);
insert into report values ('2012004', '01-03', 85);
insert into report values ('2012004', '01-04', 90);

-- 调用存储过程
call delete_graduate('2016-09-01', 120);