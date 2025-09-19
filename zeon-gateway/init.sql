create database zeon_admin;
use zeon_admin;
create table gateway_route (
    id bigint auto_increment primary key comment '主键',
    uri varchar(255) not null comment '路由地址',
    predicates text comment '断言',
    filters text comment '过滤器',
    order_by int default 0 comment '优先级',
    description varchar(255) default null comment '描述',
    enabled tinyint default 1 comment '开关',
    service_name varchar(255) not null comment '服务名称',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间',
    version int default 0 comment '版本号'
);