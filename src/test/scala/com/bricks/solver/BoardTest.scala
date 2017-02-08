package com.bricks.solver

import java.io.File
import javax.imageio.ImageIO
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BoardTest extends FunSuite {

  test("Solve example for images.") {
    // Generate the board from the image.
    var board = Board(ImageIO.read(new File("./bricks.png")))
    assert(board.labels.values.groupBy(identity).size == 6)

    // Repeatedly apply the solution to the board.
    board.solve() match {
      case Some(solution) =>
        solution.foreach(i => board = board.remove(i.neighborhood(board)))
        assert(board.labels.isEmpty)
        println(solution)
      case None => fail()
    }
  }

  test("Solve example for manually entered board.") {
    // Generate the board from a two-dimensional array.
    var board = Board(Array(
      Array(1, 1, 2, 1, 3, 3, 1, 3, 2, 3),
      Array(1, 2, 2, 2, 3, 2, 1, 2, 2, 2),
      Array(1, 2, 2, 2, 1, 2, 1, 3, 3, 1),
      Array(2, 1, 2, 2, 1, 2, 1, 1, 2, 3),
      Array(1, 1, 1, 1, 3, 2, 2, 1, 2, 3),
      Array(2, 2, 1, 3, 3, 2, 2, 3, 1, 1),
      Array(2, 2, 3, 3, 3, 1, 2, 1, 1, 1),
      Array(2, 2, 3, 3, 3, 3, 2, 3, 3, 1),
      Array(1, 2, 3, 3, 1, 3, 2, 2, 3, 1),
      Array(1, 1, 3, 3, 3, 3, 1, 2, 2, 1)
    ))

    // Repeatedly apply the solution to the board.
    board.solve() match {
      case Some(solution) =>
        solution.map(_.neighborhood(board)).foreach(i => board = board.remove(i))
        assert(board.labels.isEmpty)
        println(solution)
      case None => fail()
    }
  }

  test("Remove collapses rows and columns.") {
    val board = Board(3, 3, Map(
      Index(0, 0) -> 1,
      Index(0, 1) -> 2,
      Index(1, 0) -> 1,
      Index(2, 0) -> 2,
      Index(2, 1) -> 2
    ))

    val group = Index(0, 0).neighborhood(board)
    assert(group.size == 2)
    assert(group.contains(Index(0, 0)))
    assert(group.contains(Index(1, 0)))

    assert(board.remove(group) == Board(3, 3, Map(
      Index(0, 0) -> 2,
      Index(1, 0) -> 2,
      Index(1, 1) -> 2
    )))
  }

}
