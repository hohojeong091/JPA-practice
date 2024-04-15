package com.ohgiraffers.section01.entitymanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagerGeneratorTests {

    @Test
    @DisplayName("엔티티 매니저 팩토리 생성 확인")
    void testGenerateEntityManagerFactory() {
        //given 주어진 것

        //when 어떤 상황에서
        EntityManagerFactory factory = EntityManagerFactoryGenerator.getInstance();

        //then 결과
        assertNotNull(factory);
    }

    @Test
    @DisplayName("엔티티 매니저 팩토리 싱글톤 인스턴스 확인")
    void testIsEntityManagerFactorySingletonInstance() {
        //given 주어진 것

        //when 어떤 상황에서
        EntityManagerFactory factory1 = EntityManagerFactoryGenerator.getInstance();
        EntityManagerFactory factory2 = EntityManagerFactoryGenerator.getInstance();

        //then 결과
        assertEquals(factory1, factory2);
        assertEquals(factory1.hashCode(), factory2.hashCode());
    }

    @Test
    @DisplayName("엔터티 매니저 생성 확인")
    void testGenerateEntityManager() {
        //given

        //when
        EntityManager entityManager = EntityManagerGenerator.getInstance();

        //then
        assertNotNull(entityManager);
    }

    @Test
    @DisplayName("엔터티_매니저_스코프_확인")
    void testGenerateEntityManagerLifeCycle() {
        //given

        //when
        EntityManager entityManager1 = EntityManagerGenerator.getInstance();
        EntityManager entityManager2 = EntityManagerGenerator.getInstance();

        //then
        assertNotEquals(entityManager1, entityManager2);
        assertNotEquals(entityManager1.hashCode(), entityManager2.hashCode());
    }

}
