package com.distribute.core

import javax.security.auth.Refreshable

/**
 * A dictionary is a map that may be transactionally modified.
 */
trait Dictionary {

  def refresh(): Unit

}
