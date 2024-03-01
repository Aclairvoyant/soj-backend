package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjdddd.sojbackend.model.dto.post.PostQueryRequest;
import com.sjdddd.sojbackend.model.entity.Post;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子服务测试
 *
 * @author <a href="https://github.com/lisjdddd">程序员鱼皮</a>
 * @from <a href="https://sjdddd.icu">编程导航知识星球</a>
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    @Test
    void searchFromEs() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setUserId(1L);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}
