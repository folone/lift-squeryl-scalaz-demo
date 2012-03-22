package info.folone.model

import org.squeryl.Schema
import net.liftweb.squerylrecord.RecordTypeMode._

object MySchema extends Schema {
  val users = table[User]

  on(users)(u =>
    declare(u.userName.~.is(unique)))

}
