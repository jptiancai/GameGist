package com.imop.lj.test.battle;

public class VecTest {

	public static void main(String[] args) {
		Vec A = new Vec(1);
		Vec B = new Vec(2);
		B = A;
		B.x = 3;
		System.out.println(A.x);
	}
}
