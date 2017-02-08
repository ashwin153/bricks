package com.distribute.core.log

case class Record[T](id: Long, payload: T)