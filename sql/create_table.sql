# 数据库初始化

-- 创建库
create database if not exists soj;

-- 切换库
use soj;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    email        varchar(256)                           not null comment '邮箱',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    lastLoginTime DATETIME DEFAULT NULL COMMENT '最后登录时间',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) COLLATE = utf8mb4_unicode_ci comment '题目提交';


-- 用户已经解决题目数
DROP TABLE IF EXISTS `question_solve`;
CREATE TABLE `question_solve`
(
    `id`         bigint                                                         NOT NULL COMMENT 'id',
    `userId`     bigint                                                         NOT NULL COMMENT '用户id',
    `questionId` bigint                                                         NOT NULL COMMENT '题目id',
    `title`      varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '题目标题',
    `tags`       varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目标签',
    `createTime` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    `isDelete`   tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) COLLATE = utf8mb4_unicode_ci COMMENT = '用户已经解决题目数';

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    picture    text                               null comment '图片列表（json 数组）',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 帖子评论表
CREATE TABLE if not exists post_comment
(
    id         bigint                             NOT NULL AUTO_INCREMENT COMMENT 'id',
    postId     bigint                             NOT NULL COMMENT '帖子 id',
    userId     bigint                             NOT NULL COMMENT '创建用户 id',
    parentId   bigint   DEFAULT NULL COMMENT '父评论 id，如果是顶级评论则为NULL',
    content    text                               NOT NULL COMMENT '评论内容',
    createTime datetime DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime datetime DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   tinyint                            NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (id),
    index idx_postId (postId),
    index idx_parentId (parentId)
) COLLATE = utf8mb4_unicode_ci COMMENT ='帖子评论';


-- 公告表
create table if not exists announcement
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       not null comment '公告标题',
    content    text                               not null comment '公告内容',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status     int      default 0                 not null comment '公告状态（0 - 可见、1 - 隐藏）',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_announcementId (id)
) COLLATE = utf8mb4_unicode_ci comment '公告';


-- 题目评论表
CREATE TABLE IF NOT EXISTS question_comment
(
    id         BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    questionId BIGINT                             NOT NULL COMMENT '题目 id',
    userId     BIGINT                             NOT NULL COMMENT '创建用户 id',
    content    TEXT                               NOT NULL COMMENT '评论内容',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_postId (questionId),
    INDEX idx_userId (userId)
) COMMENT '题目评论' COLLATE = utf8mb4_unicode_ci;

-- 题单表
DROP TABLE IF EXISTS `problem_set`;
CREATE TABLE `problem_set`
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT COMMENT '题单id',
    `name`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '题单名称',
    `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '题单描述',
    `isPublic`    tinyint                                                        NOT NULL DEFAULT 1 COMMENT '是否公开 0-私有 1-公开',
    `isOfficial`  tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否官方题单 0-用户题单 1-官方题单',
    `userId`      bigint                                                         NOT NULL COMMENT '创建者id',
    `createTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_id` (`userId` ASC) USING BTREE,
    INDEX `idx_is_public` (`isPublic` ASC) USING BTREE,
    INDEX `idx_is_official` (`isOfficial` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1938571584115912707
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题单'
  ROW_FORMAT = Dynamic;

-- 题单-题目关联表
DROP TABLE IF EXISTS `problem_set_question`;
CREATE TABLE `problem_set_question`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `problemSetId` bigint   NOT NULL COMMENT '题单id',
    `questionId`   bigint   NOT NULL COMMENT '题目id',
    `sortOrder`    int      NOT NULL DEFAULT 0 COMMENT '题目排序',
    `createTime`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint  NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_question_set_id` (`problemSetId` ASC) USING BTREE,
    INDEX `idx_question_id` (`questionId` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题单-题目关联'
  ROW_FORMAT = Dynamic;

-- 用户每日打卡表
CREATE TABLE IF NOT EXISTS user_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    userId BIGINT NOT NULL COMMENT '用户ID',
    checkinDate DATE NOT NULL COMMENT '打卡日期',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    UNIQUE KEY uniq_user_date (userId, checkinDate),
    INDEX idx_user_id (userId)
) COMMENT='用户每日打卡表' COLLATE = utf8mb4_unicode_ci;

-- 管理员通知表
DROP TABLE IF EXISTS `admin_sys_notice`;
CREATE TABLE `admin_sys_notice`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`       VARCHAR(255)             DEFAULT NULL COMMENT '标题',
    `content`     TEXT COMMENT '内容',
    `type`        VARCHAR(64)              DEFAULT NULL COMMENT '指定发送用户类型',
    `state`       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否已拉取给用户 0-未拉取 1-已拉取',
    `recipientId` BIGINT UNSIGNED          DEFAULT NULL COMMENT '接收通知的用户ID',
    `adminId`     BIGINT UNSIGNED          DEFAULT NULL COMMENT '发送通知的管理员ID',
    `createTime`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    `problemSetId` BIGINT DEFAULT NULL COMMENT '关联题单id',
    PRIMARY KEY (`id`),
    KEY `idx_recipientId` (`recipientId`),
    KEY `idx_adminId` (`adminId`),
    KEY `idx_state` (`state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='管理员通知表';

-- 消息提醒表
DROP TABLE IF EXISTS `msg_remind`;
CREATE TABLE `msg_remind`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `action`        VARCHAR(64)     NOT NULL COMMENT '动作类型，如Like_Post、Like_Discuss、Discuss、Reply等',
    `sourceId`      BIGINT UNSIGNED          DEFAULT NULL COMMENT '消息来源ID，讨论ID或比赛ID',
    `sourceType`    VARCHAR(64)              DEFAULT NULL COMMENT '事件源类型：Discussion、Contest等',
    `sourceContent` VARCHAR(255)             DEFAULT NULL COMMENT '事件源内容，如回复内容、评论标题等',
    `quoteId`       BIGINT UNSIGNED          DEFAULT NULL COMMENT '引用上一级评论或回复ID',
    `quoteType`     VARCHAR(64)              DEFAULT NULL COMMENT '引用上一级类型：Comment、Reply',
    `url`           VARCHAR(255)             DEFAULT NULL COMMENT '事件发生地点链接',
    `state`         TINYINT         NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    `senderId`      BIGINT UNSIGNED          DEFAULT NULL COMMENT '操作者ID',
    `recipientId`   BIGINT UNSIGNED          DEFAULT NULL COMMENT '接收用户ID',
    `createTime`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`      TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_recipientId` (`recipientId`),
    KEY `idx_senderId` (`senderId`),
    KEY `idx_state` (`state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='消息提醒表';

-- 用户系统通知表
DROP TABLE IF EXISTS `user_sys_notice`;
CREATE TABLE `user_sys_notice`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `noticeId`    BIGINT UNSIGNED          DEFAULT NULL COMMENT '系统通知ID',
    `recipientId` BIGINT UNSIGNED          DEFAULT NULL COMMENT '接收通知的用户ID',
    `type`        VARCHAR(32)              DEFAULT NULL COMMENT '消息类型，sys-系统通知，mine-我的信息',
    `state`       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    `createTime`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_noticeId` (`noticeId`),
    KEY `idx_recipientId` (`recipientId`),
    KEY `idx_state` (`state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户系统通知表';
