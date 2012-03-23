package info.folone {
  package snippet {

    import net.liftweb._
    import http._
    import util._
    import Helpers._
    import common.{ Box, Full, Empty }
    import java.util.Calendar
    import java.text.SimpleDateFormat
    import scalaz._
    import Scalaz._
    import model.User

  class UserSnippet {

      private val formatter = new SimpleDateFormat("M/dd/yyyy")
      private var email: String      = ""
      private var pass: String       = ""
      private var verifyPass: String = ""
      private var firstname: String  = ""
      private var lastname: String   = ""
      private var sex: Boolean       = false
      private var address: String    = ""
      private var zip: String        = ""
      private var country: String    = ""
      private var tel: String        = "+31 "
      private var birthday: String   = ""

      def login: CssSel = {
        var uchargepass = ""
        var pass  = ""
        "name=uname"  #> SHtml.text(email, email = _)   &
        "name=pwd"    #> SHtml.password(pass, pass = _) &
        "type=submit" #> SHtml.onSubmit { _ ⇒
          // Applicative
          val validationResult =
            (stringEmpty_?("email")(email)   |@|
             stringEmpty_?("password")(pass) |@|
             emailCorrect_?(email))          { _ + " " + _ + " " + _ }

          validationResult match {
              case Failure(x) ⇒ S.error("Not Logged in. " + x)
              case _ ⇒ {
                User.logIn(email, pass) ? S.redirectTo("/", () ⇒
                  S.notice("Successful.")) | S.error("Wrong email/password.")
              }
          }
        }
      }


      def register: CssSel = {
        bindVars & "#cancel_button" #> SHtml.onSubmit(_ ⇒ S.redirectTo("/")) &
                   "#signup_button" #> SHtml.onSubmit(_ ⇒ {
          val validateList =
            stringEmpty_?("email")(email)         ::
            emailCorrect_?(email)                 ::
            stringEmpty_?("Firstname")(firstname) ::
            stringEmpty_?("Lastname")(lastname)   ::
            stringEmpty_?("zip")(zip)             ::
            stringEmpty_?("Country")(country)     ::
            phoneNumberCorrect_?(tel)             ::
            dateFormatCorrect_?(birthday)         ::
            stringsSame_?(("pass", "verifyPass"))((pass, verifyPass)) :: Nil
          // Monad
          val validationResult = validateList.map { _.liftFailNel }
                     .sequence[({type λ[α] = ValidationNEL[String, α]})#λ, String]
          validationResult match {
              case Failure(x) ⇒ S.error("User not created. " + x.shows)
              case _ ⇒ {
                val bday = Calendar.getInstance
                bday.setTime(formatter.parse(birthday))
                val res = User.register(email=email, password=pass, 
                                        firstname=firstname, lastname=lastname,
                                        sex=sex, address=address,
                                        zip=zip, country=country, tel=tel, birthday=bday)
                S.redirectTo("/", () ⇒
                  S.notice("Successful."))
              }
          }
        })
      }

    // -- Common bind stuff
    private def bindVars = {
        val currentUser = User.currentUser
        currentUser foreach { u ⇒
          email      = u.email.is
          firstname  = u.firstName.is
          lastname   = u.lastName.is
          sex        = u.sex.is
          address    = u.address.is
          zip        = u.zip.is
          country    = u.country.is
          tel        = u.tel.is
          u.birthday.is foreach { x ⇒ birthday = formatter.format(x.getTime) }
        }
        if(!currentUser.isDefined)
          birthday = formatter.format(Calendar.getInstance.getTime)

        "name=email"      #> SHtml.text(email, email = _)               &
        "name=pwd"        #> SHtml.password(pass, pass = _)             &
        "name=verify-pwd" #> SHtml.password(verifyPass, verifyPass = _) &
        "name=firstname"  #> SHtml.text(firstname, firstname = _)       &
        "name=lastname"   #> SHtml.text(lastname, lastname = _)         &
        "name=sex"        #> SHtml.select(("male", "male") :: ("female", "female") :: Nil,
                                          sex ? Full("female") | Full("male"),
                                          _ match {
                                            case "male" ⇒ sex = false
                                            case _      ⇒ sex = true
                                          }) &
        "name=address"    #> SHtml.text(address, address = _)               &
        "name=zip"        #> SHtml.text(zip, zip = _)                       &
        "name=country"    #> SHtml.select(("Netherlands", "Netherlands") :: 
                                          ("Belgium", "Belgium")         :: Nil,
                                          Full(country), country = _) &
        "name=tel"        #> SHtml.text(tel, tel = _)                       &
        "name=birthday"   #> SHtml.text(birthday, birthday = _)
      }

    // -- Validation methods
    private val validationNetherlandsNumber = """(([+]31)[ -_]*(\d{9}))""".r
    private val validationBelgiumNumber = """(([+]32)[ -_]*(\d{9}))""".r
    private val validationEmail = """(?i)\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b""".r

    private def stringEmpty_? = (name: String) ⇒ (_:String).isEmpty ?
      (name + " should not be empty").fail[String] | (name + " ok").success[String]

    private def stringsSame_? = (names: (String, String)) ⇒ (strings: (String, String)) ⇒
      strings match {
          case (a, b) if a === b ⇒ a.success[String]
          case _                 ⇒
            (names._1 + " and " + names._2 + " should be the same.").fail[String]
      }

    private def phoneNumberCorrect_? = (number: String) ⇒
      number match {
        case validationNetherlandsNumber(_, _, _) if (country == "Netherlands") ⇒
          number.success[String]
        case validationBelgiumNumber(_, _, _) if (country == "Belgium") ⇒
          number.success[String]
        case _ ⇒
          "Not the right phone number format. The correct format for Netherlands is +31 123456789. or +32 for Belgium".fail[String]
      }

    private def dateFormatCorrect_? = (date: String) ⇒ try {
      formatter.parse(date)
      date.success[String]
    } catch { case _ ⇒ "Birthday is of wrong format.".fail[String] }

    private def emailCorrect_? = (email: String) ⇒
      validationEmail findFirstIn email match {
          case Some(email) ⇒ email.success[String]
          case _           ⇒ "Not really an email.".fail[String]
      }

  }
  }
}
