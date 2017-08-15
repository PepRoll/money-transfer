package me.peproll.moneytransfer.services

import me.peproll.moneytransfer.model.User

class UserServiceSpec extends BaseServiceSpec {

  test("all return Seq(User(1, Mark...), User(2, Harrison...), User(3, Carrie...))") {
    userService.all.map { result =>
      result shouldEqual Seq(
        User(id = Some(1), firstName = "Mark", lastName = "Hamill"),
        User(id = Some(2), firstName = "Harrison", lastName = "Ford"),
        User(id = Some(3), firstName = "Carrie", lastName = "Fisher")
      )
    }
  }

  test("byId return Mark Hamill") {
    userService.byId(1).map { result =>
      result shouldEqual Some(
        User(id = Some(1), firstName = "Mark", lastName = "Hamill")
      )
    }
  }

  test("byId return None for unknown user") {
    userService.byId(999).map { result =>
      result shouldEqual None
    }
  }

  test("byId return None for deleted User") {
    for {
      _ <- userService.delete(1)
      user <- userService.byId(1)
    } yield user shouldEqual None
  }

  test("remain 1 account after delete Mark Hamill") {
    for {
      affectedUsers <- userService.delete(1)
      remainAccounts <- accountService.all.map(_.length)
    } yield (affectedUsers, remainAccounts) shouldEqual (1 -> 1)
  }

  test("update Mark Hamill - change name") {
    for {
      user <- userService.byId(1).map(_.get)
      c <- userService.update(user.copy(firstName = "Stas"))
      affectedUser <- userService.byId(1)
    } yield (c, affectedUser) shouldEqual (1 -> Some(User(Some(1), "Stas", "Hamill")))
  }

  test("save Kenny Baker") {
    for {
      aff <- userService.save(User(firstName = "Kenny", lastName = "Baker"))
      all <- userService.all
    } yield (aff, all.length) shouldEqual (1 -> 4)
  }

}