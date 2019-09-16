## Bowling Score Calculator 


[![platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21s)
[![License: ISC](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/ISC)

A bowling score calculator application written in Kotlins. The score calculator provides powerful functionality and it allows for a swift and dynamic calculation of score for each of the frames of a bowling game. The app allows to track the score of multiples players at once.

![Bowling calculator themes][BowlingCalculatorImage]

[BowlingCalculatorImage]: https://github.com/EudyContreras/Bowling-Score-Calculator/blob/readme-branch/images/demo_image.png


## How does it work?

The score calculator works by implementing the rules of score calculation described here [Score Calculation](https://slocums.homestead.com/gamescore.html)

## What does it feature:

### Code
* Written in Kotlin
* State manager
* Clean testable code
* Good naming of functions and variables
* Well designed (short and precise) functions
* Well designed classes
* Use of Android architectural components
* Use Dao and shared preferences for persistance

### Application
* Fast and clean UI.
* Multiple themes to choose from dynamically.
* Morph animations using a mini version of my Motion Morphing lib. Soon to be public.
* Ability to individually reset the scores for each player.
* One well-design screen using Material Design Principles.
* Frame highlighting using depth.
* Frame locking when active.
* Selectable closed frames for score editing.
* Score info board which shows the current total score.
* Score info board which shows the max possible score which can be achieved based on current state.
* Score info board which shows the current frame the player is in.
* Easily add, remove and rename bowlers.
* Bulk add multiple bowlers.
* Morphable dialogs.

## How does the algorithm for calulating the score looks like?

The algorithm used for calculating the score goes somewhat like this:

```text
1. Get all frames
2. Reset the bonus points and the added points from previous
3. Loop through each frame
4. If the frame being looped has rolls
5. If the current frame in the loop has a strike
6. If there are two next rolls
7. Add the points achieved on the next two rolls as a bonus
8. Else add the points from next roll
9. Else if the frame has a spare
10. If there is a next roll
11. Add the points achieved by the next roll as a bonus
12. If the current frame in the loop is not the first frame
13. Add the points achieved by the last frame to the current frame
```

## Example tests:

```kotlin
    @Test
    fun exampleRollTestOne() {

        val bowler = Bowler()
        bowler.init()
        bowler.reset()

        val strike = 10
        val spare = 6 to 4
        val other = 4 to 0

        bowler.performRoll(strike)

        bowler.performRoll(spare.first)
        bowler.performRoll(spare.second)

        bowler.performRoll(other.first)
        bowler.performRoll(other.second)

        val totalPoints = bowler.getComputedScore()

        assert(totalPoints == 38)
    }

    @Test
    fun exampleRollTestTwo() {

        val bowler = Bowler()

        val strike = 10

        for (counter in 0..12) {
            bowler.performRoll(strike)
        }

        val totalPoints = bowler.getComputedScore()

        assert(totalPoints == 300)
    }
```

## Bowling scoring basics:

* **A frame:** A bowling game has ten frames and each frames allows for a maximun number of throws.

* **An open frame:** This is when a frame remains to be cleared. The player has not yet knocked down all the 10 pins but still has chances remaining. 

* **A closed frame:** This is when a frame has either been cleared or when the player has spent of his/her chances. The player may or may not have knocked down all the 10 pins. 

* **A throw:** Each throw can yield a **Spare**, a **Strike** or a **Miss**.

* **The score:** A bowling game can yield a maximun score of 300. The bowling score is retroactively calculated as the previous throws affect the total score of the game.

## Symbols and Information
  * **Strike** usually represented as an **X** is when the player knocks down all the pins during the first chance of a frame.
  * **Spare** usually represented as a **/** is when the player knocks down the rest of the remaining pins a next try.
  * **Score** the amount of pins knocked down once neither a Strike nor a Spare was achieved.
  * **Miss** usually represented as a **0** is when the player does not knock down any pins upon throwing.
  
## Authors:

**Eudy Contreras**

## Contact:

If you wish to contact you may reach me through my [Linked](https://www.linkedin.com/in/eudycontreras/) or my [Email](EudyContrerasRosario@gmail.com)

## License:

This project is licensed under the MIT License - see the [Licence](https://github.com/EudyContreras/Bowling-Score-Calculator/blob/readme-branch/LICENSE) file for details
