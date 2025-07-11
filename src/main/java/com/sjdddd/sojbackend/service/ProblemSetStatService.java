package com.sjdddd.sojbackend.service;

/**
 * 题单统计服务
 * @author sjdddd
 */
public interface ProblemSetStatService {
    
    /**
     * 获取用户在题单中的完成统计（基于question_solve表）
     * @param userId 用户ID
     * @param problemSetId 题单ID
     * @return 完成统计 {completed: 已完成数, total: 总数}
     */
    ProblemSetProgressStat getUserProgressStat(Long userId, Long problemSetId);
    
    /**
     * 题单进度统计
     */
    class ProblemSetProgressStat {
        private Integer completed;
        private Integer total;
        
        public ProblemSetProgressStat(Integer completed, Integer total) {
            this.completed = completed;
            this.total = total;
        }
        
        public Integer getCompleted() {
            return completed;
        }
        
        public void setCompleted(Integer completed) {
            this.completed = completed;
        }
        
        public Integer getTotal() {
            return total;
        }
        
        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}