# Five Field Kono

## Check the report.pdf for screenshots

Students with student code ending with:  
0..4 - **Dara** (Nigerian logic game)  
5..9 - **Five Field Kono** (Korean logic game)  

Project git repo: **iti0213-2019s-hw1**  

## Dara

https://en.wikipedia.org/wiki/Dara_(game)  
https://www.youtube.com/watch?v=_O3_K7CCYFA  

## Kono

https://en.wikipedia.org/wiki/Five_Field_Kono  
https://www.youtube.com/watch?v=T1lBtF1Pilk

### HW1-Leg1

Deadline: **xx.yy.2020 23:59:59**  

UI has to support rotation. And be responsive.  

```plaintext
+-+
|A|  
|B|
+-+  
```

vs  

```plaintext
+-----+
| A B |
+-----+
```

A - statistics  
B - game board  

Game board implemented with buttons.  
Make it look nice!  

Buttons for game board have to be squares at all times (equal sides)!  
Statistics screen has minimum height (or width in landscape).  
Game board should use rest of the free space.  

No code needed yet, just XML-s for screens.  

### HW1-Leg2

Deadline: **xx.yy.2020 23:59:59**  

Game engine has to be in separate kotlin class (don't mix ui and business logic).  
State saving/restoring for rotation.  
UX - make it nice and usable!  
Implement all gaming modes - 2 player, 1 player vs AI, AI vs AI.  
And it must be possible to choose, which side starts.  
AI implementation must be a something better than just random legal moves.  
Special glory to these, who will implement Minimax with A-B pruning.  
Extra super glory for multi-threaded AI implementation.  
