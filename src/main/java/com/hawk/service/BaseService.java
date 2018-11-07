package com.hawk.service;

import com.hawk.repo.BaseRepo;

import java.util.List;

/**
 * BaseService
 *
 * @author wangshuguang
 * @since 2018/04/03
 */
public abstract class BaseService<T, R extends BaseRepo<T, Long>> {

    protected R repo;

    BaseService() {}

    BaseService(R repo) {
        this.repo = repo;
    }

    public T findOne(Long id) {
        return repo.getOne(id);
    }

    public T save(T entity) {
        return repo.save(entity);
    }

    public void save(List<T> entityList) {
        repo.saveAll(entityList);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public void delete(T entity) {
        repo.delete(entity);
    }
}
