package com.ohgiraffers.section02.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityMangerCRUDTests {

    private EntityManagerCRUD crud;

    @BeforeEach
    void initManger() {
        this.crud = new EntityManagerCRUD();

    }

    @DisplayName("메뉴 코드로 메뉴 조회 테스트")
    @ParameterizedTest //하나의 테스트 메소드로 여러 개의 파라미터에 대한 테스트가 가능함.
    @CsvSource({"1,1", "2,2", "3,3"})
    void testFindMenuByMenucode(int menuCode, int expected) {


     //when
     Menu foundMenu = crud.findMenuByMenuCode(menuCode);

     //then
     assertEquals(expected, foundMenu.getMenuCode());
        System.out.println("foundMenu = " + foundMenu);

    }


    private static Stream<Arguments> newMenu() {
        return Stream.of(
                Arguments.of(
                        "신메뉴",
                        35000,
                        4,
                        "Y"
                        //메뉴이름, 메뉴가격, 카테고리코드, 주문가능여부 순서대로
                        //테스트를 여러번 하고 싶으면 arguments를 여러개 전달하면 됨.
                )
        );

    }
    @DisplayName("새로운 메뉴 추가 테스트")
    @ParameterizedTest
    @MethodSource("newMenu")
    void testRegist(String menuName, int menuPrice, int categoryCode, String orderableStatus) {
        // when
        Menu newMenu = new Menu(menuName, menuPrice, categoryCode, orderableStatus);
        Long count = crud.saveAndReturnAllCount(newMenu);

        //then
        assertEquals(28, count);
     }

    @DisplayName("메뉴 이름 수정 테스트")
    @ParameterizedTest
    @CsvSource("1, 변경된 이름")
    void testModigyMenuName(int menuCode, String menuName) {
        //when
        Menu modifyMenu = crud.modifyMenuName(menuCode, menuName);

        //then
        assertEquals(menuName, modifyMenu.getMenuName());
    }

    @DisplayName("메뉴 삭제 테스트")
    @ParameterizedTest
    @ValueSource(ints = {29})
    void testRemoveMenu(int menuCode) {}
    //when
    Long count = crud.removeAndReturnAllCount(menuCode);

    //then
    assertEquals(29, count);

}
