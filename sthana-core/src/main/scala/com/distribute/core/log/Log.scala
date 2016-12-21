package com.distribute.core.log

import scala.concurrent.Future

trait Log[T] {

  def append(payload: T): Future[Long]

  def read(from: Long, until: Long): Future[Seq[Record[T]]]

}

//package com.twitter.clustermanager.database
//
//import com.twitter.util.Future
//
//trait Log {
//
//  /**
//   * Asynchronously appends some data to the end of the log. Returns a future containing the record
//   * number, or an exception if the data could not be inserted. Record numbers are guaranteed to be
//   * monotonically incrementing integers beginning from zero.
//   *
//   * @param data Data to be written to the log.
//   * @return Future containing the record number of the entry.
//   */
//  def append(data: Array[Byte]): Future[Int]
//
//  /**
//   * Asynchronously returns a future containing at most 'number' data entries from the log beginning
//   * from the specified starting record, or an empty sequence if there are no entries to be read.
//   * By default, this method returns all the records in the log.
//   *
//   * @param from Starting record number; inclusive (defaults to the beginning of the log).
//   * @param until Ending record number; exclusive (defaults to the end of the log).
//   * @return Future containing an ordered sequence of log records.
//   */
//  def read(from: Int = 0, until: Int = Int.MaxValue): Future[Seq[Array[Byte]]]
//
//}
