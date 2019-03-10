package com.cache;

import com.cache.cacheOfMemcached.AccountService;
import com.cache.oldCache.Account;
import com.google.common.collect.Lists;
import net.spy.memcached.MemcachedClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MemcacheTest {

    /**
     * 0. 测试memcached是否可用
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        MemcachedClient c = new MemcachedClient(new InetSocketAddress("127.0.0.1",11211));
        c.set("someKey",3600,"test");
        Object myObject = c.get("someKey");
        System.out.println(myObject);
    }


    /**
     * 2. 测试simple-spring-memcached的@ReadThroughSingleCache注解，不用写代码让既有代码支持memcached缓存
     */
    @Test
    public void testSingleCache() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-memcached.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        // 第一次查询，应该走数据库
        System.out.println("first query...");
        Account account = s.getAccountByName("somebody1");
        // 第二次查询，应该不查数据库，直接返回缓存的值
        System.out.println("second query...");
        s.getAccountByName("somebody1");
    }


    /**
     * 2. 测试simple-spring-memcached的@ReadThroughMultiCache注解，一次性查询多个key的缓存值
     */
    @Test
    public void testMultiCache() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-memcached.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        initData(s);
        // 第一次查询，key为multi1、multi2的不走缓存，key为multi3进入缓存
        System.out.println("first query...");
        System.out.println(s.getAccountsByName(Lists.newArrayList("multi1","multi2","multi3")));
        // 第二次查询，直接返回缓存的值
        System.out.println("second query...");
        System.out.println(s.getAccountsByName(Lists.newArrayList("somebody1","somebody2","somebody3")));
    }

    /**
     * 3. 测试simple-spring-memcached的@ReadThroughAssignCache注解，查询指定key值的缓存
     */
    @Test
    public void testAssignCache() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-cache-memcached.xml");// 加载 spring 配置文件

        AccountService s = (AccountService) context.getBean("accountServiceBean");
        // 第一次查询，key为multi1、multi2的不走缓存，key为multi3进入缓存
        System.out.println("first query...");
        System.out.println(s.getDefaultAccount());
        // 第二次查询，直接返回缓存的值
        System.out.println("second query...");
        System.out.println(s.getDefaultAccount());
    }

    private void initData(AccountService accountService) {
        accountService.getAccountByName("multi1");
        accountService.getAccountByName("multi2");
    }

}
