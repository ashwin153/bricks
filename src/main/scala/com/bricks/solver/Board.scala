package com.bricks.solver

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable

/**
 * A brick-pop board. Boards are represented as sparse matrices in which each index is assigned at
 * most one label. When indexes are removed from a board, they cause columns and rows to collapse
 * which guarantees that the board indices always form a single connected-component.
 *
 * @param height Board height.
 * @param width Board width.
 * @param labels Labels for indices.
 * @tparam L Type of label.
 */
case class Board[L](
  height: Int,
  width: Int,
  labels: Map[Index, L]
) {

  require(labels.keys.forall(i => i.y >= 0 && i.y < this.height), "Indices must be in the board.")
  require(labels.keys.forall(i => i.x >= 0 && i.x < this.width),  "Indices must be in the board.")

  /**
   * Removes the specified indicies from the board, and collapses columns and rows to ensure that
   * there always exists a single connected component; in other words, each index must be reachable
   * from (0, 0). Returns a new board with the updated indices.
   *
   * @param indices Indices to remove.
   * @return A collapsed board without the specified indices.
   */
  def remove(indices: Seq[Index]): Board[L] = {
    var update = mutable.Map.empty ++ this.labels
    indices.filter(update.contains).sorted.reverse.foreach { index =>
      // Remove the index from the updated board, and find all indexes that are above it.
      update -= index

      val above = (index.y + 1 until this.height)
        .map(Index(index.x, _))
        .filter(update.contains)

      // If there are any indices above, then shift them all down by one row. Otherwise, if no such
      // index exists and the index is on the zeroth row, then shift all indices to the right over
      // by one. Because the indices are iterated over from right to left and top to bottom, the
      // processing of any particular index should not effect the processing of future indices.
      if (above.nonEmpty) {
        above.foreach { i =>
          update += Index(i.x, i.y - 1) -> update(i)
          update -= i
        }
      } else if (above.isEmpty && index.y == 0) {
        for {
          y <- 0 until height
          x <- index.x + 1 until width
          i = Index(x, y)
          if update.contains(i)
        } {
          update += Index(x - 1, y) -> update(i)
          update -= i
        }
      }
    }

    // Construct a copy of the board with the updated labels.
    this.copy(labels = update.toMap)
  }

  /**
   * Recursively generates the sequence of indices whose neighborhoods must be removed in order to
   * solve the board. If no such sequence exists, then this method returns None.
   *
   * @return Sequence of indices that solve the board, or None.
   */
  def solve(): Option[Seq[Index]] = {
    if (this.labels.isEmpty) {
      // If the board has no labels remaining, then it is considered a solved board.
      Some(Seq.empty)
    } else if (this.labels.values.groupBy(identity).exists(_._2.size == 1)) {
      // If any label has only one remaining, then the board is impossible to solve.
      None
    } else {
      // We can vastly increase the performance of the algorithm by only checking each neighborhood
      // once. If an index has been visited, we know its entire neighborhood has been visited.
      val unvisited = mutable.Queue(this.labels.keys.toSeq: _*)
      val neighbors = mutable.Buffer.empty[Seq[Index]]

      while (unvisited.nonEmpty) {
        val index = unvisited.dequeue()
        neighbors += index.neighborhood(this)
        unvisited.dequeueAll(neighbors.last.contains)
      }

      // Try solving each non-unitary neighborhood in parallel until any one is solved.
      neighbors.filter(_.size > 1).iterator
        .map(i => (i.head, this.remove(i).solve()))
        .find(_._2.isDefined)
        .map(i => i._1 +: i._2.get)
    }
  }

}

object Board {

  /**
   * Constructs a board from the provided image. Index labels are inferred by the rgb color value of
   * the squares in the image. On my iPhone 6s, the location of the boxes begins 330 pixels down
   * from the top-left corner and each box is 63x63 pixels with a 12 pixel padding to the right and
   * left.
   *
   * @param img Image to process.
   * @return Board representation of the image.
   */
  def apply(img: BufferedImage): Board[Color] =
    apply(Array.tabulate(10, 10) { (r, c) =>
      new Color(img.getRGB(75 * c + 32, 330 + 75 * r + 32))
    })

  /**
   * Constructs a board from the provided two-dimensional array. Arrays are required to be non-empty
   * and must have consistent dimensions; in other words, each row must have the same length.
   *
   * @param grid 2D array to process.
   * @return Board representation of the array.
   * @tparam L Type of board labels.
   */
  def apply[L](grid: Array[Array[L]]): Board[L] = {
    require(grid.length > 0 && grid(0).length > 0, "Grid is non-empty.")
    require(grid.forall(_.length == grid(0).length), "Grid must have consistent dimensions.")

    Board(grid.length, grid(0).length, (for {
      r <- grid.indices
      c <- grid(r).indices
    } yield Index(c, grid.length - r - 1) -> grid(r)(c)).toMap)
  }

}