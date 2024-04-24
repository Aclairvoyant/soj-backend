package com.sjdddd.sojbackend.job.once;

import com.sjdddd.sojbackend.esdao.PostEsDao;
import com.sjdddd.sojbackend.model.dto.post.PostEsDTO;
import com.sjdddd.sojbackend.model.entity.Post;
import com.sjdddd.sojbackend.service.PostService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 全量同步帖子到 es
 */

@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        List<Post> postList = postService.list();
        log.info("======================================");
        log.info("postList size: {}", postList.size());
        if (CollectionUtils.isEmpty(postList)) {
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
//            postEsDao.saveAll(postEsDTOList.subList(i, end));
            try {
                postEsDao.saveAll(postEsDTOList.subList(i, end));
            } catch (RuntimeException e) {
                log.error("Failed to execute bulk operation: {}", e.getMessage(), e);
                // Optionally, log the response body or more details if available
            }

        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
