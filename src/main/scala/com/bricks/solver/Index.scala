package com.bricks.solver

import scala.collection.mutable
import scala.math.Ordered.orderingToOrdered

/**
 * An index into the position on the board. Index (0, 0) represents the bottom-left corner. Indices
 * are necessarily non-negative. Indices are ordered first by their x coordinate and then by their
 * y coordinate.
 *
 * @param x Horizontal position.
 * @param y Vertical position.
 */
case class Index(x: Int, y: Int) extends Ordered[Index] {

  override def compare(that: Index): Int =
    (this.x, this.y).compare(that.x, that.y)

  /**
   * Returns all neighboring indices in the board that have the same label as this index.
   * Implementation performs a variation of Breadth First Search to locate all 4-connected indices
   * of the same label.
   *
   * @param board Board to search through.
   * @tparam T Type of board.
   * @return All neighboring indices.
   */
  def neighborhood[T](board: Board[T]): Seq[Index] = {
    val queue = mutable.Queue(this)
    var visited = mutable.Set.empty[Index]

    while (queue.nonEmpty) {
      val index = queue.dequeue()
      if (!visited.contains(index)) {
        val neighbors = Seq(
          Index(index.x - 1, index.y + 0),
          Index(index.x + 1, index.y + 0),
          Index(index.x + 0, index.y - 1),
          Index(index.x + 0, index.y + 1)
        )

        neighbors
          .filter(board.labels.get(_).exists(_ == board.labels(this)))
          .foreach(queue.enqueue(_))
        visited += index
      }
    }

    (visited += this).toSeq
  }

}