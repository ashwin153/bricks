package com.bricks.solver

/**
 *
 * @param row
 * @param col
 */
case class Move(
  row: Int,
  col: Int
) {

  def adjacent(board: Board): Seq[Move] =
    Seq(
      Move(this.row - 1, this.col),
      Move(this.row + 1, this.col),
      Move(this.row, this.col - 1),
      Move(this.row, this.col + 1)
    ) .filter(m => m.row >= 0 && m.row < board.height)
      .filter(m => m.col >= 0 && m.col < board.width)
      .filter(m => board(m.row)(m.col) == board(this.row)(this.col))

}