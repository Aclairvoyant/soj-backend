package com.sjdddd.sojbackend.constant;

public interface MsgConstant {
    
    String ACTION_LIKE_POST = "LIKE_POST";
    String ACTION_COMMENT_POST = "COMMENT_POST";
    String ACTION_REPLY_COMMENT = "REPLY_COMMENT";
    String ACTION_LIKE_COMMENT = "LIKE_COMMENT";
    String ACTION_SYSTEM_NOTICE = "SYSTEM_NOTICE";
    String ACTION_FOLLOW = "FOLLOW";
    String ACTION_MENTION = "MENTION";
    
    String SOURCE_TYPE_POST = "POST";
    String SOURCE_TYPE_COMMENT = "COMMENT";
    String SOURCE_TYPE_REPLY = "REPLY";
    String SOURCE_TYPE_SYSTEM = "SYSTEM";
    String SOURCE_TYPE_USER = "USER";
    
    String QUOTE_TYPE_COMMENT = "COMMENT";
    String QUOTE_TYPE_REPLY = "REPLY";
    String QUOTE_TYPE_POST = "POST";
    
    int STATE_UNREAD = 0;
    int STATE_READ = 1;
    
    int DELETE_NO = 0;
    int DELETE_YES = 1;
}