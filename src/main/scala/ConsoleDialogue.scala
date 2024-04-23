//> using dep dev.zio::zio::2.0.22

enum Dialogue:
  case Ask(question: String, yesContinuation: Dialogue, noContinuation: Dialogue)
  case Stop(conclusion: String)

  def greetFirst(name: String): Dialogue =
    Ask(s"Welcome $name, are you ready to continue?", this, Stop(s"See you later $name."))
end Dialogue

import Dialogue.*
import zio.*
import java.io.IOException

val exampleDialogue: Dialogue =
  Ask("Do you know ZIO?",
    Ask("Do you like it?",
      Stop("Good!"),
      Stop("I can't believe it!")),
    Stop("What a pity!"))

object ConsoleDialogue extends ZIOAppDefault:

  private def consoleDialogue(dialogue: Dialogue): IO[IOException, Unit] = dialogue match
    case Ask(question: String, yesContinuation: Dialogue, noContinuation: Dialogue) =>
      for
        bool <- askBooleanQuestion(question)
        _    <- if bool then consoleDialogue(yesContinuation)
                else consoleDialogue(noContinuation)
      yield ()
    case Stop(conclusion: String) =>
      Console.printLine(conclusion)

  private def askBooleanQuestion(question: String): IO[IOException, Boolean] =
    for
      _    <- Console.printLine(question)
      bool <- getBool()
    yield bool

  private def getBool(): IO[IOException, Boolean] =
    for
      input <- Console.readLine
      bool  <- ZIO.fromOption(makeBool(input)) orElse
               (Console.printLine("Please type 'y' or 'n'") zipRight getBool())
    yield bool

  private def makeBool(s: String): Option[Boolean] =
    if s == "y" then Some(true)
    else if s == "n" then Some(false)
    else None

  private def greetFirstConsoleDialogue(dialogue: Dialogue): IO[Exception, Unit] =
    for
      name <- Console.readLine("What is your name?\n")
      _    <- consoleDialogue(dialogue.greetFirst(name))
    yield ()

  def run: IO[Exception, Unit] = greetFirstConsoleDialogue(exampleDialogue)

end ConsoleDialogue
