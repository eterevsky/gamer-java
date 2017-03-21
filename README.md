Fun with Monte-Carlo generic game engines
=========================================

This project has two purposes: I'm using it to teach myself Java and to learn about [general game playing](http://en.wikipedia.org/wiki/General_game_playing). Mainly, the [Monte-Carlo tree search](http://en.wikipedia.org/wiki/Monte_Carlo_tree_search) algorithms are used. Here is a good [survey of Monte-Carlo techniques](http://www.cameronius.com/cv/mcts-survey-master.pdf) in relation to game playing.

I'm starting with testing the engines on [Gomoku](http://en.wikipedia.org/wiki/Gomoku), because it has really simple rules, but my ultimate goal is to play some imperfect information games with chance.

What's already working
----------------------

All the main interfaces are defined in `gamer/def`. A new game can be added by implementing the interfaces `gamer.def.Game`, `gamer.def.State` and/or `gamer.def.PositionMut` (first is immutable, second is mutable) and `gamer.der.Move`. To add a new playing engine, the `ComputerPlayer` interface should be implemented.

There are working implentations of Gomoku and Chess. On the side of the engines, the most advanced one is `MonteCarloUct`.
