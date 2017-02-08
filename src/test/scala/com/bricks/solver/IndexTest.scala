package com.bricks.solver

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IndexTest extends FunSuite {

  test("Ordered by x first, and y second.") {
    assert(Index(0, 0) < Index(0, 1))
    assert(Index(0, 1) < Index(1, 0))
    assert(Index(0, 1) < Index(0, 2))
    assert(Index(0, 3) == Index(0, 3))
  }
  
}
