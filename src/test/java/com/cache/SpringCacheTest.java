package com.cache;

import com.cache.cacheOfAnno.AccountService;
import com.cache.oldCache.Account;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringCacheTest {

    /**
     * 1. 测试spring-cache的@Cacheable注解，不用写代码让既有代码支持缓存
     */
    @Test
    public void testCacheOfAnno() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-anno.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        // 第一次查询，应该走数据库
        System.out.print("first query...");
        s.getAccountByName("somebody");
        // 第二次查询，应该不查数据库，直接返回缓存的值
        System.out.print("second query...");
        s.getAccountByName("somebody");
    }

    /**
     * 2. 测试spring-cache的@CacheEvict注解，清空缓存，以保证缓存数据的可靠性。
     */
    @Test
    public void testCacheOfAnnoByUpdate() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-anno.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        // 第一次查询，应该走数据库
        System.out.print("first query...");
        s.getAccountByName("somebody");
        // 第二次查询，应该不查数据库，直接返回缓存的值
        System.out.print("second query...");
        s.getAccountByName("somebody");
        System.out.println();

        System.out.println("start testing clear cache...");    // 更新某个记录的缓存，首先构造两个账号记录，然后记录到缓存中
        Account account1 = s.getAccountByName("somebody1");
        Account account2 = s.getAccountByName("somebody2");
        // 开始更新其中一个
        account1.setId(1212);
        s.updateAccount(account1);
        s.getAccountByName("somebody1");// 因为被更新了，所以会查询数据库
        s.getAccountByName("somebody2");// 没有更新过，应该走缓存
        s.getAccountByName("somebody1");// 再次查询，应该走缓存
        // 更新所有缓存
        s.reload();
        s.getAccountByName("somebody1");// 应该会查询数据库
        s.getAccountByName("somebody2");// 应该会查询数据库
        s.getAccountByName("somebody1");// 应该走缓存
        s.getAccountByName("somebody2");// 应该走缓存

    }

    /**
     * 按照条件操作缓存
     */
    @Test
    public void testCacheOfAnnoWithCondition() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-anno.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        // 第一次查询，应该走数据库
        System.out.print("first query...");
        s.getAccountByNameWithCondition("somebody");// 长度大于 4，不会被缓存
        s.getAccountByNameWithCondition("sbd");// 长度小于 4，会被缓存
        System.out.print("second query...");

        s.getAccountByNameWithCondition("somebody");// 还是查询数据库
        s.getAccountByNameWithCondition("sbd");// 会从缓存返回
    }
    /**
     * 3. 测试spring-cache的@Cacheable注解key的生成，如果有多个参数，如何进行 key 的组合。
     */
    @Test
    public void testCacheOfAnnoWithComsopseKey() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-anno.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        s.getAccount("somebody", "123456", true);// 应该查询数据库
        s.getAccount("somebody", "123456", true);// 应该走缓存
        s.getAccount("somebody", "123456", false);// 应该走缓存
        s.getAccount("somebody", "654321", true);// 应该查询数据库
        s.getAccount("somebody", "654321", true);// 应该走缓存
    }
    /**
     * 4. 测试spring-cache的@CachePut注解，可以保证更新方法被执行，且结果一定会被缓存。
     */
    @Test
    public void testCacheOfAnnoWithUpdateCache() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-anno.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");

        Account account = s.getAccountByName("someone");
        account.setPassword("123");
        s.updateAccountAndCache(account);
        account.setPassword("321");
        s.updateAccountAndCache(account);
        account = s.getAccountByName("someone");
        System.out.println(account.getPassword());
    }
    /**
     * 5. 测试spring-cache的自定义缓存方案
     */
    @Test
    public void testCacheOfAnnoWithCacheCustom() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "Spring-cache-anno-customer.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");

        Account account = s.getAccountByName("someone");
        System.out.println("passwd="+account.getPassword());
        account = s.getAccountByName("someone");
        System.out.println("passwd="+account.getPassword());
    }

}
