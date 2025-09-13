/*Jeffards Dungeon Adventure; ICS3U1 Culminating Project
 *By Jason Tran
 * 
 * The program:
 * You start in a menu screen that has 3 options; start game, other screen or leave. 
 * If you press 1 on your keyboard, you start the inGame and alive loops that loop until you die or win.
 * If you press 2 on your keyboard, you are taken to the other screen where instructions and controls are displayed. From this screen only, if you press 4, you will return to the menu
 * if you press 3 on your keyboard, you are taken to a still end game screen that runs until you close the application.
 * You control a character in a topdown style dungeon game. Your character moves along the x and y axis exsclusively.
 * The controls are W to move up, A to move left, S to move down, D to move down and K to attack.
 * The attack works by creating a new object everytime the k key is pressed and this is called the slash, it has its own coordinates that are checked for collision between enemies.
 * The attack only registers when there has not been a key press of the character k in the last 250 milliseconds leading to a strategic playstyle of timing hits.
 * The game is a constant state of math, always adding and subtracting health between yourself and enemies.
 * The goal of the game is to defeat the final boss and in order to do that, you have to upgrade your players health and damage statistics to be a match for the boss.
 * You upgrade your statistics through colliding with chests on the map and through killing 2 minibosses.
 * There are 5 enemies: 
 * enemyA; moves on a single linear axis. This is an enemy that has low damage and low health. They give you Health points when you attack it through the use of the lifeSteal mechanic. 
 * enemyB; tracking enemy that relocates to a random position on the map using Math.random() every time it collides with you or you land a hit on it.
 * miniBoss1, miniBoss2, finalBoss; they all have higher damage and health than enemyAs and enemyBs. 
 * The 3 bosses for the most part move along a single axis at a time being either the horizontal or vertical axis.
 * They have the chance to move diagonally if they collide with a wall and have an axis switch at the same time.
 * The axis switch is a boolean value that returns true if the random number (0-10000 or so) is less than 5. If the switch is true, x values are traded for y values and vice versa.
 * 
 * A large portion of the code is collision detection between the entities and their surroundings, this is achieved through the distanceLinePoint method and the distance method.
 * The menu screen is split up into 3 methods; menu(), otherScreenMethod(), exitMethod(). These methods are mainly to organize the code when switching back and forth between menu and game. 
 * These methods just draws the images and the strings that are used to help the user understand the game.
 * I chose to put all of my variables as global variables so that there would be easy access between each of the 9 methods for each area of the game.
 * The functions that are named with the world "lock" in it serve a useful purpose. We only want a portion of code to run once and that is not possible normally because the entire game is under a loop.
 * When we put a lock function on to something, like the levelLock, the commands under the levelLock are only run once and then the levelLock value is updated to someting else.
 * This was very useful when figuring out how to travel between each of the 9 unique areas of the map. 
 * When you want to re-enter a previous area of the map, the levelLock is going to be different and therefore that means that you are not restarting the whole area
 * For each part of the map that has 2 or more entrances, there is a positive levelLock meaning that if you enter this area chronologically and for the first time, everything is setup a certain way (chests not taken, enemies at full health)
 * there is also a negative levelLock meaning that if you enter this area from an area that is ahead of this one, you wont restart the whole area and your character will be in the same spot as the entrance they came through
 * 
 * The alive and inGame loops end from the player reaching 0 or less health or defeating the finalBoss, of which the appropriate endGame screen will be presented.
 * When this happens, every variable is returned to its original value and the player is prompted to once again select 1, 2, or 3 on their keyboard.
 */


import hsa_ufa.Console;
import java.awt.*;
import javax.swing.*;
import java.util.*;
public class CulminatingTranJason{


//Global variables that are useful when making methods


  static Console c = new Console(1000, 1000, "Jeffard's Dungeon Adventure");
  static Color backgroundColor = new Color(18, 12, 36);
  static Font f1 = new Font("Jokerman", Font.BOLD, 30);
  static Font f2 = new Font ("Serif", Font.BOLD | Font.ITALIC, 20);
  static Image jeffardLeft = loadImage("Jeffards (1).gif");
  static Image jeffardRight = loadImage("Jeffards (1)Right.png");
  static Image jeffardShieldLeft = loadImage("jeffardShieldLeft.png");
  static Image jeffardShieldRight = loadImage("jeffardShieldRight.png");
  static Image jeffardAxeLeft = loadImage("jeffardAxeLeft.png");
  static Image jeffardAxeRight = loadImage("jeffardAxeRight.png");
  static Image jeffardFullLeft = loadImage("jeffardFullLeft.png");
  static Image jeffardFullRight= loadImage("jeffardFullRight.png");
  static Image enemyA = loadImage("enemyA.png");
  static Image enemyB = loadImage("enemyB.png");
  static Image miniBoss1Left = loadImage("miniBoss1Left.png");
  static Image miniBoss1Right = loadImage("miniBoss1Right.png");
  static Image miniBoss2Left = loadImage("miniBoss2Left.png");
  static Image miniBoss2Right = loadImage("miniBoss2Right.png");
  static Image finalBossLeft = loadImage("finalBossLeft.png");
  static Image finalBossRight = loadImage("finalBossRight.png");
  static Image slashUp = loadImage("slash.png"); //I tried using the rotating feature with the drawImage command, it makes the other graphics not animate properly So i manually made a graphic for each direction
  static Image slashLeft = loadImage("slash (1).png");
  static Image slashRight = loadImage("slash (2).png");
  static Image slashDown = loadImage("slash (3).png");
  static Image chest = loadImage("chest.png");
  static Image spikeDown = loadImage("spikeDown.png");
  static Image spikeUp = loadImage("spikeUp.png");
  static Image spikeLeft = loadImage("spikeLeft.png");
  static Image spikeRight = loadImage("spikeRight.png");

  
//Variables used for timer
  static long lastKeypressTime = 0;
  static long currentTimeMillis = 0;


  //location and statistics for jeffard
  static int x = 200;
  static int y = 50;
  static int width = 64;
  static int height = 64;
  static int jXc = x + width / 2;
  static int jYc = y + height / 2;
  static int dir = 0;
  static int health = 150;
  static int healthMax = 150; //To be updated with buffs 
  static int damage = 20; //To be updated with buffs
  static int lifeSteal = 0;


  //location of the attack swing (slash)
  static int slashX = 0;
  static int slashY = 0;
  static int slashWidth = 60;
  static int slashHeight = 30;
  static int slashXc = slashX + slashWidth / 2;
  static int slashYc = slashY + slashHeight / 2;
  static boolean drawSlash = true;
  static boolean quarterSecond = false; // timer for the cooldown of the slash
  
  
  //location of chests
  static int chestWidth = 100;
  static int chestHeight = 100;
  static int[] chestX = new int[5];
  static int[] chestY = new int[5];
  static int[] chestXc = new int[5];
  static int[] chestYc = new int[5];
  static boolean[] chestClaimed = new boolean[5];
  static int chestRoll = 0;
  static int chestLock = 0;
               
  //location and statistics for enemyAs
  static int eAWidth = 80;
  static int eAHeight = 40;
  static int numEnemiesA = 20; //populating the array
  static int[] eAX = new int[numEnemiesA];       
  static int[] eAY = new int[numEnemiesA];
  static int[] eAXc = new int[numEnemiesA];
  static int[] eAYc = new int[numEnemiesA];
  static int[] eAdx = new int[numEnemiesA];
  static int[] eAdy = new int[numEnemiesA];
  static int[] eAHealth = new int[numEnemiesA];

  //location and statistics for enemyBs'
  static int eBWidth = 40;
  static int eBHeight = 80;
  static int numEnemiesB = 20; // populating the array
  static int[] eBX = new int[numEnemiesB];
  static int[] eBY = new int[numEnemiesB];
  static int[] eBXc = new int[numEnemiesB] ;
  static int[] eBYc = new int[numEnemiesB];
  static int[] eBHealth = new int[numEnemiesB];
  


  //The following enemy bosses behave like enemyA, they bounce off the walls moving in or more directions and they also have a slash just like the player. This slash will have a longer timer.

  static int bossDisplacement = 0; 
  static int bossDir = 0;
  static int bossSlashX = 0;
  static int bossSlashY = 0;
  static int bossSlashXc = bossSlashX + slashWidth / 2;
  static int bossSlashYc = bossSlashY + slashHeight / 2;
  static boolean threeSeconds = false; //cooldown timer for bosses slash
  static boolean drawBossSlash = true;
  static long lastBossAttack = 0;


  //location and statistics for miniBoss1, the alive variable is set as true because jeffards maxHealth statistic is buffed when miniBoss1 is not alive.
  static boolean miniBoss1Alive = true;
  static int boss1Width = 100;
  static int boss1Height = 130;
  static int boss1Health = 300;
  static int boss1Damage = 15;
  static int boss1X = 0;
  static int boss1Y = 0;
  static int boss1dx = 0;
  static int boss1dy = 0;
  static int boss1Xc = boss1X + boss1Width / 2;
  static int boss1Yc = boss1Y + boss1Height / 2; 
  static int boss1Lock = 0; //when the boss is defeated, the statistics only buff you once, not for every iteration of the loop.
  
  //location and statistics for miniBoss2, the alive variable is set as true because jeffards damage statistic is buffed when miniBoss1 is not alive.
  static boolean miniBoss2Alive = true;
  static int boss2Width = 100;
  static int boss2Height = 130;
  static int boss2Health = 250;
  static int boss2Damage = 30;
  static int boss2X = 0;
  static int boss2Y = 0;
  static int boss2dx = 0;
  static int boss2dy = 0;
  static int boss2Xc = boss2X + boss2Width / 2;
  static int boss2Yc = boss2Y + boss2Height / 2;
  static int boss2Lock = 0; //when the boss is defeated, the statistics only buff you once, not for every iteration of the loop.
  
  //location and statistics for finalBoss, the alive variable is set as true because when the finalBoss is not alive, you win the game.
  static boolean finalBossAlive = true;
  static int boss3Width = 120;
  static int boss3Height = 150;
  static int boss3Health = 500;
  static int boss3Damage = 30;
  static int boss3X = 0;
  static int boss3Y = 0;
  static int boss3dx = 0;
  static int boss3dy = 0;
  static int boss3Xc = boss3X + boss3Width / 2;
  static int boss3Yc = boss3Y + boss3Height / 2;
  



  //First set of boolean values to determine what part of the game the player is in.
  static boolean inMenu = true;
  static boolean inGame = false;
  static boolean inOtherScreen = false;

  //Second set of boolean values to determine which part of the map the player is in. There are supposedly 10 parts to the map.
  static boolean inA1 = true;
  static boolean inA2 = false;
  static boolean inA3 = false;
  static boolean inA4 = false;
  static boolean inA5 = false;
  static boolean inA6 = false;
  static boolean inA7 = false;
  static boolean inA8 = false;
  static boolean inA9 = false;


  //Misc. variables
  static int levelLock = 0; // locks up the players spawn coordinates everytime they are in a part of a map so that they can move with their coordinates being updated and not stuck.
  
  static boolean alive = false;
  static boolean doorShut = false;
  static char keyChar = c.getKeyChar();
  static boolean switchD = false; //switches the bosses movement direction

  // Buttons for main menu
 
  public static void main(String[] args)
  {
      keyChar = c.getKeyChar();
        
      while (inMenu == true)
      {
      keyChar = c.getKeyChar();
      menu();


       // Checks which button was clicked
       if (keyChar == '1')
       {
         inGame = true;
         alive = true;
         levelLock = 0;
       }
       else if (keyChar == '2')
       {
         inOtherScreen = true;
       }
       else if (keyChar == '3')
       {
        exitMethod();
        inMenu = false;
       }


      // Series of loops that "exit" the main menu and go to their respective game options.
      // Quotation marks around exit because technically the code is still in the menu loop.

      while (inOtherScreen)
      {
        keyChar = c.getKeyChar();

        otherScreenMethod();
        
         if (keyChar == '4')
          {
            inOtherScreen = false;
          }
      }

      if (inGame)
      {
          alive = true;
          
          
          
          while(alive){

          quarterSecond = (currentTimeMillis - lastKeypressTime) >= 250; //jeffard attack timer
          threeSeconds = (currentTimeMillis - lastBossAttack) >= 3000; //bosses attack timer
          //update current time;
          currentTimeMillis = System.currentTimeMillis();
          keyChar = c.getKeyChar(); // Get keyboard input

          //player movement keys
              if (keyChar == 'w')
              {
                y-=2;
                dir = 90;
              }
              else if (keyChar =='a')
              {
                x-=2;
                dir = 180;
              }
              else if (keyChar == 's')
              {
                 y+=2;
                 dir = 270;
              }
              else if (keyChar =='d')
              {
                x+=2;
                dir = 0;
              }


              else if (keyChar == 'k')
              {
                drawSlash = true;
                lastKeypressTime = currentTimeMillis;
              }
          
    //update the players position on the map (collisions depend on this coordinate)
               jXc = x + width / 2;
               jYc = y + height / 2;
               
               //update the weapon slashes position on the map
                 if (dir == 0)
                 {
                   slashX = jXc + 50;
                   slashY = jYc - 25;
                 }
                 else if (dir == 90)
                 {
                   slashX = jXc - 25;
                   slashY = jYc - 100;
                 }
                 else if (dir == 180)
                 {
                   slashX = jXc - 100;
                   slashY = jYc - 25;
                 }
                 else if (dir == 270)
                 {
                   slashX = jXc - 25;
                   slashY = jYc + 50;
                 }
   
                 slashXc = slashX + slashWidth / 2;
                 slashYc = slashY + slashHeight / 2;

            
              c.setFont(f1);
              c.setColor(Color.WHITE);
              c.drawString("Damage: " + damage, 100,100);
              c.drawString("MaxHealth:  " + healthMax, 100,150);
              c.drawString("LifeSteal:  " + lifeSteal, 100,200);



              if (inA1)
              {
                a1Method();               
              } 
              else if (inA2)
              {
                a2Method();
              }
              else if (inA3)
              {
                a3Method();
              }
              else if (inA4)
              {
                a4Method();
              }
              else if (inA5)
              {
                a5Method();
              }
              else if (inA6)
              {
                a6Method();
              }
              else if (inA7)
              {
                a7Method();
              }
              else if (inA8)
              {
                a8Method();
              }
              else if (inA9)
              {
                a9Method();
              }


              //player gains stat buffs from beating bosses

                if(miniBoss1Alive == false && boss1Lock == 0)
                {
                  healthMax += 100;
                  health = healthMax;
                  boss1Lock = 1;
                }

                if(miniBoss2Alive == false && boss2Lock == 0)
                {
                  damage += 20;
                  boss2Lock = 1;
                }

              if (health > healthMax) // This prevents overheal
                  {
                    health = healthMax;
                  }

              lifeSteal = damage / 2; //updates according to damage

              //Checks for if the alive loop and inGame loop are false
               if (health <= 0)
               {
                 alive = false;
                 inGame = false;
                 inA1 = true; 
                 levelLock = 0;
                 chestLock = 0;
                 boss1Lock = 0;
                 boss2Lock = 0;
                 miniBoss1Alive = true;
                 boss1Health = boss1Health;
                 boss2Health = boss2Health;
                 boss3Health = boss3Health;
                 miniBoss2Alive = true;
                 health = 150; 
                 healthMax = 150;
                 damage = 20;
                 c.clear();
                 c.setColor(Color.BLACK);
                 c.drawString("YOU DIED!", 200,400);
                 delay(2000);
                 chestClaimed[0] = false;
                    chestX[0] = 800;
                    chestY[0] = 100;
                    chestXc[0] = chestX[0] + chestWidth / 2;
                    chestYc[0] = chestY[0] + chestHeight / 2;
                    chestLock = 1;
                 
               }


                // Added September 12, 2025
                // Since the program only runs as fast as the CPU and given that my CPU is alot stronger than the one I had when creating this game, 
                // this delay makes it so that the game can be ran by computers with stronger CPU's. Imperfect solution for an imperfect code structure.
                delay(1);

            }//end of alive loop

      } //end of inGame loop


    } // end of inMenu loop
           
         
         
  } // end of main method



   public static void menu ()
   {
  Image startButton = loadImage("startButton.gif");
  Image otherButton = loadImage("otherButton.gif");
  Image exitButton = loadImage("leaveButton.gif");
  Image button1 = loadImage("button1.png");
  Image button2 = loadImage("button2.png");
  Image button3 = loadImage("button3.png");
  Image title = loadImage("titleScreen.png");
  Color menuColor = new Color(44, 37, 59);

        synchronized(c)
        {
          c.setBackgroundColor(menuColor);
          c.clear();
          c.drawImage(title, 50,0, 900, 400);
          c.drawImage(jeffardLeft, 700,350,256,256);
          c.drawImage(startButton,350,400, 248, 120);
          c.drawImage(otherButton,350,550, 248, 120);
          c.drawImage(exitButton,350,700, 248, 120);
          c.drawImage(button1, 200,400, 120, 120);
          c.drawImage(button2,200,550, 120, 120);
          c.drawImage(button3,200,700, 120, 120);
        }
       
   }

   public static void otherScreenMethod()
   {
    Image button4 = loadImage("button4.png");
    Image exitButton = loadImage("leaveButton.gif");
    Image otherScreenText = loadImage("otherScreen.png");

    synchronized(c)
      {
        c.clear();  
        c.drawImage(otherScreenText,0 ,0,1000,1000);
        c.drawImage(exitButton,400,800, 248, 120);
        c.drawImage(button4,250,800, 120, 120);
      }
   }
 
   public static void exitMethod ()
   {
      Font bigFont = new Font("Dialog", Font.BOLD, 60);
      c.setFont(bigFont);
      c.setColor(Color.white);
      synchronized(c)
      {
        c.clear();
        c.drawString("Thank you for playing! ", 150, 300);
      }
   }



   public static void a1Method()
   {
    if (levelLock == 0) //If you enter A1 normally through the menu, execute these commands
                {
                  health = healthMax;
                  x = 200;
                  y = 50;
                  levelLock = 1;
                  eAX[0] = 850;
                  eAY[0] = 300;
                  eAdx[0] = 1;
                  eAHealth[0] = 75;

                  eAX[1] = 400;
                  eAY[1] = 400;
                  eAdy[1] = 1;
                  eAHealth[1] = 75;

                  eBX[0] = (int)(Math.random()*1500);
                  eBY[0] = (int)(Math.random()*1500);
                  eBHealth[0] = 100;

                  if (chestLock == 0) // similar to how the levelLock variable works, ensures that a chest can only be accessed once
                  {
                    chestClaimed[0] = false;
                    chestX[0] = 800;
                    chestY[0] = 100;
                    chestXc[0] = chestX[0] + chestWidth / 2;
                    chestYc[0] = chestY[0] + chestHeight / 2;
                    chestLock = 1;
                  }
                  
                  
                }
      else if(levelLock == -2) //returning from A2
                {
                  x = 200;
                  y = 800;
                  levelLock = 1; //This allows entering the next part of the map to run normally, this variable is always updated when entering an area
                }
               
    //Draw the map
                synchronized(c){
                  c.setBackgroundColor(Color.GRAY);
                  c.clear();
                  c.setColor(backgroundColor);

                  c.fillRect(0, 0, 1000, 50);
                  c.fillRect(400,800,600,200);
                  c.fillRect(0,0,50,1000);
                  c.fillRect(950,0,50,800);
                  c.fillRect(600,0,100,400);

                  //draw the chest
                  if(chestClaimed[0] == false)
                  {
                    c.drawImage(chest,800,100,chestWidth,chestHeight);
                  }

                 // draw the entities

                 //series of if statements that check which image of jeffard to draw depending on the alive status of minibosses 
                 //because if jeffard kills that mini boss, he takes their piece of equipment
                 
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                  
                  c.setColor(Color.GREEN);
                  c.drawString(""+health, x + 16, y +96);
                  c.setColor(Color.YELLOW);
                  if (eAHealth[0] > 0)
                  {
                    c.drawImage(enemyA, eAX[0], eAY[0], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[0], eAX[0]+25, eAY[0]+65);
                  }
                  if (eAHealth[1] > 0)
                  {
                    c.drawImage(enemyA, eAX[1], eAY[1], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[1], eAX[1]+25, eAY[1]+65);
                  }
                  if (eBHealth[0] > 0)
                  {
                    c.drawImage(enemyB, eBX[0], eBY[0], eBWidth, eBHeight);
                    c.drawString(""+eBHealth[0], eBX[0]-5, eBY[0]+110);
                  }
                  
              //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }
                }
             
    //collision with walls for jeffard (user)
                if (distanceLinePoint(0,50, 1000, 50, jXc, jYc) < width / 2)
                {
                  y +=2;
                }
                else if (distanceLinePoint(400,800, 950, 800, jXc, jYc) < width / 2)
                {
                  y-=2;
                }
                else if (distanceLinePoint(600,400, 700, 400, jXc, jYc) < width / 2)
                {
                  y+=2;
                }
                else if (distanceLinePoint(50,0, 50, 1000, jXc, jYc) < width / 2)
                {
                  x+=2;
                }
                else if (distanceLinePoint(950,50, 950, 800, jXc, jYc) < width / 2)
                {
                  x-=2;
                }
                else if (distanceLinePoint(400,800, 400, 1000, jXc, jYc) < width / 2)
                {
                  x-=2;
                }
                else if (distanceLinePoint(600,50, 600, 400, jXc, jYc) < width / 2)
                {
                  x-=2;
                }
                else if (distanceLinePoint(700,50, 700, 400, jXc, jYc) < width / 2)
                {
                  x+=2;
                }
                else if (distanceLinePoint(50,1000,400,1000, jXc, jYc) < width / 2) 
                {
                  inA1 = false;
                  inA2 = true;
                }

    //collsion with chest[0] for jeffard
    if (distanceLinePoint(800,100, 800, 200, jXc, jYc) < width / 2 && chestClaimed[0] == false)
                {
                  chestRoll = (int)(Math.random()*2);
                  if (chestRoll == 0)
                  {
                      healthMax += 20;
                  }
                  else if (chestRoll == 1)
                  {
                      damage += 5;
                  }
                  chestClaimed[0] = true;
                }
    else if (distanceLinePoint(800,200, 900, 200, jXc, jYc) < width / 2 && chestClaimed[0] == false)
                {
                  chestRoll = (int)(Math.random()*2);
                  if (chestRoll == 0)
                  {
                      healthMax += 20;
                  }
                  else if (chestRoll == 1)
                  {
                      damage += 5;
                  }
                  chestClaimed[0] = true;

                }

    //collision with walls for enemyAs
                if (eAX[0] > 875)
                {
                  eAdx[0] = -1;
                }
                else if (eAX[0] < 700)
                {
                  eAdx[0] = 1;
                }

                if(eAY[1] > 775)
                {
                  eAdy[1] = -1;
                }
                else if(eAY[1] < 50)
                {
                  eAdy[1] = 1;
                }

    //collision for jeffard and enemyAs
    
                if (distance(jXc, jYc, eAXc[0], eAYc[0]) < width && eAHealth[0] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }

                  
                  
                  health -= 10;
                }

                if (distance(jXc, jYc, eAXc[1], eAYc[1]) < width && eAHealth[1] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                 
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }
                 
                  health -= 10;
                }

    //collision for enemyAs and weapon slash
               
                  if (distance(slashXc,slashYc, eAXc[0], eAYc[0]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[0] > 0)
                  {
                    eAHealth[0] -= damage;
                    health += lifeSteal;
                  }
                  
                  if (distance(slashXc,slashYc, eAXc[1], eAYc[1]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[1] > 0)
                  {
                    eAHealth[1] -= damage;
                    health += lifeSteal;
                  }

                  if (health > healthMax) // This prevents overheal
                  {
                    health = healthMax;
                  }

      //enemyAs' movement updates
                eAX[0] += eAdx[0];
                eAXc[0] = eAX[0] + eAWidth / 2;
                eAYc[0] = eAY[0] + eAHeight / 2;

                eAY[1] += eAdy[1];
                eAXc[1] = eAX[1] + eAWidth / 2;
                eAYc[1] = eAY[1] + eAHeight / 2;

       //collision for jeffard and enemyBs

                if(distance(jXc, jYc, eBXc[0], eBYc[0]) < (width / 2) && eBHealth[0] > 0)
                {
                  //respawns and attacks again
                  eBX[0] = (int)(Math.random()*1500);
                  eBY[0] = (int)(Math.random()*1500);
                  health -= 10;
                }
                
      //collision for enemyBs and weapon slash
                  if (distance(slashXc,slashYc, eBXc[0], eBYc[0]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[0] > 0)
                  {
                    eBHealth[0] -= damage;
                    eBX[0] = (int)(Math.random()*1500);
                    eBY[0] = (int)(Math.random()*1500);
                  }
        
      //enemyBs' movement updates

                eBXc[0] = eBX[0] + eBWidth / 2;
                eBYc[0] = eBY[0] + eBHeight / 2;


                //the enemy goes towards the player
                if(eBX[0] < x)
                {
                  eBX[0] = eBX[0] + (x - (x-1));  //1
                }
                if(eBX[0] > x)
                {            
                  eBX[0] = eBX[0] + (x - (x+1)); //1
                }
                if(eBY[0] < y)
                {
                  eBY[0] = eBY[0] + (y - (y-1)); //1
                }
                if(eBY[0] > y)
                {
                  eBY[0] = eBY[0] + (y - (y+1)); //1
                }
   }


   public static void a2Method()
   
   {
    if (levelLock == 1)
                {
                  x = 200;
                  y = 0;
                  levelLock = 2;

                  eAX[2] = 200;
                  eAY[2] = 500;
                  eAX[3] = 100;
                  eAY[3] = 800;
                  eAX[4] = 500;
                  eAY[4] = 300;
                  eAX[5] = 800;
                  eAY[5] = 700;

                  eAHealth[2] = 75;
                  eAHealth[3] = 75;
                  eAHealth[4] = 75;
                  eAHealth[5] = 75;

                  eAdx[2] = 1;
                  eAdx[3] = 1;
                  eAdy[4] = 1;
                  eAdy[5] = 1;
                  
                  if(chestLock == 1)
                  {
                    chestClaimed[1] = false;
                    chestX[1] = 700;
                    chestY[1] = 800;
                    chestXc[1] = chestX[1] + chestWidth / 2;
                    chestYc[1] = chestY[1] + chestHeight / 2;
                    chestLock = 2;
                  }
                  
                  
                }

    else if(levelLock == -3) // returning from A3
               {
                x = 200;
                y = 800;
                levelLock = 2;
               }
    else if(levelLock == -6) // returning from A5
               {
                x = 900;
                y = 550;
                levelLock = 2;
               }
                synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(400,0,600,200);
                c.fillRect(400, 400, 300, 600);


                c.fillRect(0,0,50,1000);
               
                c.fillRect(950,200,50,200);
                c.fillRect(950,700,50,200);
                c.fillRect(700,900,300,100);

                //draw the chest
                  if(chestClaimed[1] == false)
                  {
                    c.drawImage(chest,700,800,chestWidth,chestHeight);
                  }
    //draw the entities
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);
                c.setColor(Color.YELLOW);
                if (eAHealth[2] > 0)
                  {
                    c.drawImage(enemyA, eAX[2], eAY[2], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[2], eAX[2]+25, eAY[2]+65);
                  }
                  if (eAHealth[3] > 0)
                  {
                    c.drawImage(enemyA, eAX[3], eAY[3], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[3], eAX[3]+25, eAY[3]+65);
                  }
                  if (eAHealth[4] > 0)
                  {
                    c.drawImage(enemyA, eAX[4], eAY[4], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[4], eAX[4]+25, eAY[4]+65);
                  }
                  if (eAHealth[5] > 0)
                  {
                    c.drawImage(enemyA, eAX[5], eAY[5], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[5], eAX[5]+25, eAY[5]+65);
                  }
                  
                   //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }                }

    //Collision checks for jeffard

                if (distanceLinePoint(400,200,950,200,jXc,jYc) < width / 2)
                {
                  y +=2;
                }
                else if (distanceLinePoint(950,400,1000,400,jXc,jYc) < width / 2)
                {
                  y +=2;
                }
                else if (distanceLinePoint(400,400,700,400,jXc,jYc) < width / 2)
                {
                  y -=2;
                }
                else if (distanceLinePoint(700,900,950,900,jXc,jYc) < width / 2)
                {
                  y -=2;
                }
                else if (distanceLinePoint(950,700,1000,700,jXc,jYc) < width / 2)
                {
                  y -=2;
                }


                else if (distanceLinePoint(50, 0, 50, 1000, jXc,jYc) < width / 2)
                {
                  x +=2;
                }
                else if (distanceLinePoint(700, 400, 700, 900, jXc,jYc) < width / 2)
                {
                  x +=2;
                }


                else if (distanceLinePoint(400, 0, 400, 200, jXc,jYc) < width / 2)
                {
                  x -=2;
                }
                else if (distanceLinePoint(400, 400, 400, 1000, jXc,jYc) < width / 2)
                {
                  x -=2;
                }
                else if (distanceLinePoint(950, 200, 950, 400, jXc,jYc) < width / 2)
                {
                  x -=2;
                }
                else if (distanceLinePoint(950, 700, 950, 900, jXc,jYc) < width / 2)
                {
                  x -=2;
                }


                else if (distanceLinePoint(50,1100,400,1000, jXc, jYc) < width / 2) //going into A3
                                                                                                // made the y-value of the exit point 100 units higher so that
                                                                                                // coordinate the player crosses to go between levels is not the same (that leads to skipping A2)
                {
                  inA3 = true;
                  inA2 = false;                  
                }


                else if (distanceLinePoint(1100,400,1100,700, jXc, jYc) < width / 2) //going into A5
                {
                  inA5 = true;
                  inA2 = false;
                  levelLock = 6;
                }
                
                else if (distanceLinePoint(50,0,400,0, jXc, jYc) < width / 2) //returning to A1
                  {
                    inA1 = true;
                    inA2 = false;
                    levelLock = -2;
                  }

      //collsion with chest[1] for jeffard
      if (distanceLinePoint(700,800, 800, 800, jXc, jYc) < width / 2 && chestClaimed[1] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[1] = true;
      }
      else if (distanceLinePoint(800,800, 800, 900, jXc, jYc) < width / 2 && chestClaimed[1] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[1] = true;

      }
                  
      //collision with walls for enemyAs
                if (eAX[2] > 325)
                {
                  eAdx[2] = -1;
                }
                else if (eAX[2] < 50)
                {
                  eAdx[2] = 1;
                }

                if(eAX[3] > 325)
                {
                  eAdx[3] = -1;
                }
                else if(eAX[3] < 50)
                {
                  eAdx[3] = 1;
                }

                if(eAY[4] > 350)
                {
                  eAdy[4] = -1;
                }
                else if(eAY[4] < 200)
                {
                  eAdy[4] = 1;
                }

                if(eAY[5] > 875)
                {
                  eAdy[5] = -1;
                }
                else if(eAY[5] < 200)
                {
                  eAdy[5] = 1;
                }

    //collision for jeffard and enemyAs
                if (distance(jXc, jYc, eAXc[2], eAYc[2]) < width && eAHealth[2] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }
                  
                  health -= 10;
                }

                if (distance(jXc, jYc, eAXc[3], eAYc[3]) < width && eAHealth[3] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                 
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }
                 
                  health -= 10;
                }

                if (distance(jXc, jYc, eAXc[4], eAYc[4]) < width && eAHealth[4] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                 
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }
                 
                  health -= 10;
                }

                 if (distance(jXc, jYc, eAXc[5], eAYc[5]) < width && eAHealth[5] > 0)
                {
                  //knockback so that the player is not continuosly damaged
                 
                  if (dir == 0)
                  {
                    x-= 100;
                  }
                  else if (dir == 180)
                  {
                    x+= 100;
                  }
                  else if (dir == 90)
                  {
                    y+= 100;
                  }
                  else if (dir == 270)
                  {
                    y-= 100;
                  }
                 
                  health -= 10;
                }
                
    //collision for enemyAs and weapon slash
               
                  if (distance(slashXc,slashYc, eAXc[2], eAYc[2]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[2] > 0)
                  {
                    eAHealth[2] -= damage;
                    health += lifeSteal;
                  }
                  
                  if (distance(slashXc,slashYc, eAXc[3], eAYc[3]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[3] > 0)
                  {
                    eAHealth[3] -= damage;
                    health += lifeSteal;
                  }

                  if (distance(slashXc,slashYc, eAXc[4], eAYc[4]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[4] > 0)
                  {
                    eAHealth[4] -= damage;
                    health += lifeSteal;
                  }
                  
                  if (distance(slashXc,slashYc, eAXc[5], eAYc[5]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[5] > 0)
                  {
                    eAHealth[5] -= damage;
                    health += lifeSteal;
                  }     

      //enemyAs' movement updates
                eAX[2] += eAdx[2];
                eAXc[2] = eAX[2] + eAWidth / 2;
                eAYc[2] = eAY[2] + eAHeight / 2;

                eAX[3] += eAdx[3];
                eAXc[3] = eAX[3] + eAWidth / 2;
                eAYc[3] = eAY[3] + eAHeight / 2;

                eAY[4] += eAdy[4];
                eAXc[4] = eAX[4] + eAWidth / 2;
                eAYc[4] = eAY[4] + eAHeight / 2;

                eAY[5] += eAdy[5];
                eAXc[5] = eAX[5] + eAWidth / 2;
                eAYc[5] = eAY[5] + eAHeight / 2;
      
   }

public static void a3Method()
{
if (levelLock == 2)
{
x = 200;
y = 0;
levelLock = 3;

eBX[1] = (int)(Math.random()*1300); 
eBY[1] = (int)(Math.random()*1300);
eBX[2] = (int)(Math.random()*1300); 
eBY[2] = (int)(Math.random()*1300);

eBHealth[1] = 50;
eBHealth[2] = 50;

}
else if(levelLock == -4) // returning from A4
{
x = 800;
y = 600;
levelLock = 3;
}

synchronized(c){
c.setBackgroundColor(Color.GRAY);
c.clear();
c.setColor(backgroundColor);
c.fillRect(400,0,600,400);
c.fillRect(50,800,950,200);
c.fillRect(0,0,50,1000);

if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
c.setColor(Color.GREEN);
c.drawString(""+health, x + 16, y +96);
c.setColor(Color.YELLOW);
if (eBHealth[1] > 0)
{
 c.drawImage(enemyB, eBX[1], eBY[1], eBWidth, eBHeight);
 c.drawString(""+eBHealth[1], eBX[1]-5, eBY[1]+110);
}
if (eBHealth[2] > 0)
{
 c.drawImage(enemyB, eBX[2], eBY[2], eBWidth, eBHeight);
 c.drawString(""+eBHealth[2], eBX[2]-5, eBY[2]+110);
}

//draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }

              //draw the additional info
              c.setFont(f1);
              c.setColor(Color.WHITE);
              c.drawString("Damage: " + damage, 100,100);
              c.drawString("MaxHealth:  " + healthMax, 100,150);
              c.drawString("LifeSteal:  " + lifeSteal, 100,200);
}

if (distanceLinePoint(400,400,1000,400,jXc,jYc) < width / 2)
{
y +=2;
}
else if (distanceLinePoint(50,800,1000,800,jXc,jYc) < width / 2)
{
y -=2;
}
else if (distanceLinePoint(400,-200,400,400,jXc,jYc) < width / 2)
{
x -=2;
}
else if (distanceLinePoint(50,-200,50,800,jXc,jYc) < width / 2)
{
x +=2;
}
else if (distanceLinePoint(1000,400,1000,800,jXc,jYc) < width / 2)
{
inA3 = false;
inA4 = true;
}
else if (distanceLinePoint(50,-200,400,0,jXc,jYc) < width / 2) //returning to A2
                                                                     //making the collision line even futher up so that they dont get mixed up and go to innapropriate areas
{
inA2 = true;
inA3 = false;
levelLock = -3;
}

//collision for jeffard and enemyBs
if(distance(jXc, jYc, eBXc[1], eBYc[1]) < (width / 2) && eBHealth[1] > 0)
{
eBX[1] = (int)(Math.random()*1300);
eBY[1] = (int)(Math.random()*1300);
health -= 10;
}

if(distance(jXc, jYc, eBXc[2], eBYc[2]) < (width / 2) && eBHealth[2] > 0)
{
eBX[2] = (int)(Math.random()*1300);
eBY[2] = (int)(Math.random()*1300);
health -= 10;
}

//collision for enemyBs and weapon slash
if (distance(slashXc,slashYc, eBXc[1], eBYc[1]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[1] > 0)
{
eBHealth[1] -= damage;
eBX[1] = (int)(Math.random()*1300);
eBY[1] = (int)(Math.random()*1300);
}

if (distance(slashXc,slashYc, eBXc[2], eBYc[2]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[2] > 0)
{
eBHealth[2] -= damage;
eBX[2] = (int)(Math.random()*1300);
eBY[2] = (int)(Math.random()*1300);
}

//enemyBs' movement updates

eBXc[1] = eBX[1] + eBWidth / 2;
eBYc[1] = eBY[1] + eBHeight / 2;
eBXc[2] = eBX[2] + eBWidth / 2;
eBYc[2] = eBY[2] + eBHeight / 2;


//the enemy goes towards the player
if(eBX[1] < x)
{
eBX[1] = eBX[1] + (x - (x-1));  //1
}
if(eBX[1] > x)
{            
eBX[1] = eBX[1] + (x - (x+1)); //1
}
if(eBY[1] < y)
{
eBY[1] = eBY[1] + (y - (y-1)); //1
}
if(eBY[1] > y)
{
eBY[1] = eBY[1] + (y - (y+1)); //1
}

if(eBX[2] < x)
{
eBX[2] = eBX[2] + (x - (x-1));  //1
}
if(eBX[2] > x)
{            
eBX[2] = eBX[2] + (x - (x+1)); //1
}
if(eBY[2] < y)
{
eBY[2] = eBY[2] + (y - (y-1)); //1
}
if(eBY[2] > y)
{
eBY[2] = eBY[2] + (y - (y+1)); //1
}

 }

    public static void a4Method()
    {
      if (levelLock == 3)
      {
        x = 50;
        y = 600;
        levelLock = 4;

        if(chestLock == 2 || chestLock == 1)
        {
          chestClaimed[2] = false;
          chestX[2] = 500;
          chestY[2] = 700;
          chestXc[2] = chestX[2] + chestWidth / 2;
          chestYc[2] = chestY[2] + chestHeight / 2;
          chestLock = 3;
        }
      }
      else if(levelLock == -5) //returning from A5
      {
        x = 500;
        y = 100;
        levelLock = 4;
      }

      synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,0,400,400);
                c.fillRect(0,800,1000,200);
                c.fillRect(600,0,400,800);

                //draw the chest
                  if(chestClaimed[2] == false)
                  {
                    c.drawImage(chest,500,700,chestWidth,chestHeight);
                  }
                
                  //draw the spikes
                  c.drawImage(spikeDown, 0,400);
                  c.drawImage(spikeDown,100,400);
                  c.drawImage(spikeDown,200,400);
                  c.drawImage(spikeDown, 300,400);
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);


                 //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }                }

      if (distanceLinePoint(-100,500,400,500,jXc,jYc) < width / 2) //checking for collision with spikes that damage you
                {
                    y+= 100;
                    health -= 20;
                  
                }
      else if (distanceLinePoint(400,400,400,500,jXc,jYc) < width / 2)
                {
                    x += 100;
                    health -= 20;
                }
      else if (distanceLinePoint(400,0,400,400,jXc,jYc) < width / 2)
                {
                    x += 2;
                }
      else if (distanceLinePoint(600,0,600,800,jXc,jYc) < width / 2)
                {
                    x-=2;
                }   
      else if (distanceLinePoint(-100,800,600,800,jXc,jYc) < width / 2)
                {
                    y-=2;
                }              
      else if (distanceLinePoint(400,0,600,0,jXc,jYc) < width / 2) //going into A5
                {
                    inA5 = true;
                    inA4 = false;
                }   
      else if (distanceLinePoint(-100,500,-100,800,jXc,jYc) < width / 2) //returning to A3 
                {
                    inA3 = true;
                    inA4 = false;
                    levelLock = -4;
                }   

      //collsion with chest[2] for jeffard
      if (distanceLinePoint(500,700, 600, 700, jXc, jYc) < width / 2 && chestClaimed[2] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[2] = true;
      }
      else if (distanceLinePoint(500,700, 500, 800, jXc, jYc) < width / 2 && chestClaimed[2] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[2] = true;

      }

    }

    public static void a5Method()
    {

      if (levelLock == 4) //if you have entered from A4
      {
        x = 500;
        y = 900;
        levelLock = 5;

        eAX[6] = 200;
        eAY[6] = 700;
        eAX[7] = 700;
        eAY[7] = 200;
        eAdx[6] = 1;
        eAdy[7] = 1;
        eBX[5] = (int)(Math.random()*2300); 
        eBY[5] = (int)(Math.random()*2300);
        
        eAHealth[6] = 100;
        eAHealth[7] = 100;
        eBHealth[5] = 100;

      }
      else if (levelLock == 6) // if you have entered from A2
      {
        x = 100;
        y= 550;
        levelLock = 5;
        eAX[6] = 200;
        eAY[6] = 700;
        eAX[7] = 700;
        eAY[7] = 200;
        eAdx[6] = 1;
        eAdy[7] = 1;
        eBX[5] = (int)(Math.random()*2300); 
        eBY[5] = (int)(Math.random()*2300);

        eAHealth[6] = 100;
        eAHealth[7] = 100;
        eBHealth[5] = 100;

      }
      else if(levelLock == -7) //returning from A6
      {
        x = 900;
        y = 550;
        levelLock = 5; 
      }

       synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,700,100,300);
                c.fillRect(100,900,300,100);
                c.fillRect(0,0,100,400);
                c.fillRect(0,0,1000,100);
                c.fillRect(900,100,100,300);
                c.fillRect(900,700,100,300);
                c.fillRect(600,900,300,100);
                
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);

                c.setColor(Color.YELLOW);
                if (eAHealth[6] > 0)
                  {
                    c.drawImage(enemyA, eAX[6], eAY[6], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[6], eAX[6]+25, eAY[6]+65);
                  }
                  if (eAHealth[7] > 0)
                  {
                    c.drawImage(enemyA, eAX[7], eAY[7], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[7], eAX[7]+25, eAY[7]+65);
                  }

                  if (eBHealth[5] > 0)
                  {
                  c.drawImage(enemyB, eBX[5], eBY[5], eBWidth, eBHeight);
                  c.drawString(""+eBHealth[5], eBX[5]-5, eBY[5]+110);
                  }
                  
                   //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }                }

        if (distanceLinePoint(400,900,400,1100,jXc,jYc) < width / 2) 
                {
                    x+= 2;
                }
        else if (distanceLinePoint(600,900,600,1100,jXc,jYc) < width / 2) 
                {
                    x-= 2;
                }
        else if (distanceLinePoint(100,900,400,900,jXc,jYc) < width / 2) 
                {
                    y-= 2;
                }
        else if (distanceLinePoint(600,900,900,900,jXc,jYc) < width / 2) 
                {
                    y-= 2;
                }
        else if (distanceLinePoint(100,700,100,900,jXc,jYc) < width / 2) 
                {
                    x+= 2;
                }
        else if (distanceLinePoint(900,700,900,900,jXc,jYc) < width / 2) 
                {
                    x-= 2;
                }
        else if (distanceLinePoint(100,100,100,400,jXc,jYc) < width / 2) 
                {
                    x+= 2;
                }
        else if (distanceLinePoint(900,100,900,400,jXc,jYc) < width / 2) 
                {
                    x-= 2;
                }
        else if (distanceLinePoint(100,100,900,100,jXc,jYc) < width / 2) 
                {
                    y+= 2;
                }
        else if (distanceLinePoint(900,400,1000,400,jXc,jYc) < width / 2) 
                {
                    y+= 2;
                }
        else if (distanceLinePoint(-100,400,100,400,jXc,jYc) < width / 2) 
                {
                    y+= 2;
                }
        else if (distanceLinePoint(-100,700,100,700,jXc,jYc) < width / 2) 
                {
                    y-= 2;
                }
        else if (distanceLinePoint(900,700,1000,700,jXc,jYc) < width / 2) 
                {
                    y-= 2;
                }
        else if (distanceLinePoint(1000,400,1000,700,jXc,jYc) < width / 2) // going into A6
                {
                    inA6 = true;
                    inA5 = false;
                }
        else if (distanceLinePoint(-100,400,-100,700,jXc,jYc) < width / 2) // returning to A2
                {
                    inA2 = true;
                    inA5 = false;
                    levelLock = -6;
                }
        else if (distanceLinePoint(400,1100,600,1100,jXc,jYc) < width / 2) // returning to A4
                {
                    inA4 = true;
                    inA5 = false;
                    levelLock = -5;
                }

     //collision with walls for enemyAs
    if (eAX[6] > 875)
    {
      eAdx[6] = -1;
    }
    else if (eAX[6] < 100)
    {
      eAdx[6] = 1;
    }

    if(eAY[7] > 875)
    {
      eAdy[7] = -1;
    }
    else if(eAY[7] < 100)
    {
      eAdy[7] = 1;
    }

    //collision for jeffard and enemyAs
    if (distance(jXc, jYc, eAXc[6], eAYc[6]) < width && eAHealth[6] > 0)
    {
      //knockback so that the player is not continuosly damaged
      if (dir == 0)
      {
        x-= 100;
      }
      else if (dir == 180)
      {
        x+= 100;
      }
      else if (dir == 90)
      {
        y+= 100;
      }
      else if (dir == 270)
      {
        y-= 100;
      }
      
      health -= 10;
    }

    if (distance(jXc, jYc, eAXc[7], eAYc[7]) < width && eAHealth[7] > 0)
    {
      //knockback so that the player is not continuosly damaged
      
      if (dir == 0)
      {
        x-= 100;
      }
      else if (dir == 180)
      {
        x+= 100;
      }
      else if (dir == 90)
      {
        y+= 100;
      }
      else if (dir == 270)
      {
        y-= 100;
      }
      
      health -= 10;
    }

    //collision for enemyAs and weapon slash

      if (distance(slashXc,slashYc, eAXc[6], eAYc[6]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[6] > 0)
      {
        eAHealth[6] -= damage;
        health += lifeSteal;
      }
      
      if (distance(slashXc,slashYc, eAXc[7], eAYc[7]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[7] > 0)
      {
        eAHealth[7] -= damage;
        health += lifeSteal;
      }

      if (health > healthMax) // This prevents overheal
      {
        health = healthMax;
      }

    //enemyAs' movement updates
    eAX[6] += eAdx[6];
    eAXc[6] = eAX[6] + eAWidth / 2;
    eAYc[6] = eAY[6] + eAHeight / 2;

    eAY[7] += eAdy[7];
    eAXc[7] = eAX[7] + eAWidth / 2;
    eAYc[7] = eAY[7] + eAHeight / 2;

      //collision for jeffard and enemyBs
      if(distance(jXc, jYc, eBXc[5], eBYc[5]) < (width / 2) && eBHealth[5] > 0)
      {
      eBX[5] = (int)(Math.random()*2300);
      eBY[5] = (int)(Math.random()*2300);
      health -= 10;
      }

      //collision for enemyBs and weapon slash
      if (distance(slashXc,slashYc, eBXc[5], eBYc[5]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[5] > 0)
      {
      eBHealth[5] -= damage;
      eBX[5] = (int)(Math.random()*2300);
      eBY[5] = (int)(Math.random()*2300);
      }

      //enemyBs' movement updates

        eBXc[5] = eBX[5] + eBWidth / 2;
        eBYc[5] = eBY[5] + eBHeight / 2;

        //the enemy goes towards the player
        if(eBX[5] < x)
        {
        eBX[5] = eBX[5] + (x - (x-2));  //2
        }
        if(eBX[5] > x)
        {            
        eBX[5] = eBX[5] + (x - (x+2)); //2
        }
        if(eBY[5] < y)
        {
        eBY[5] = eBY[5] + (y - (y-2)); //2
        }
        if(eBY[5] > y)
        {
        eBY[5] = eBY[5] + (y - (y+2)); //2
        }

    }

    public static void a6Method() 
    {                             
      if (levelLock == 5) // entering from A5
                {
                  x = 100;
                  y= 550;

                  eAX[8] = 100;
                  eAY[8] = 700;
                  eAdy[8] = 1;
                  eAX[9] = 200;
                  eAY[9] = 200;
                  eAdy[9] = 1;
                  eAX[10] = 900;
                  eAY[10] = 200;
                  eAdy[10] = 1;
                  eAX[11] = 800;
                  eAY[11] = 700;
                  eAdy[11] = 1;

                  eAHealth[8] = 200;
                  eAHealth[9] = 200;
                  eAHealth[10] = 200;
                  eAHealth[11] = 200;
  
                  levelLock = 7;
                }
      else if (levelLock == -8) //returning from A7
                {
                  x = 500;
                  y = 100;
                  levelLock = 7;
                }
      else if (levelLock == -9) //returning from A8
                {
                  x = 500;
                  y = 900;
                  levelLock = 7;
                }
      else if (levelLock == -10) //returning from A9
                {
                  x = 900;
                  y = 550;
                  levelLock = 7;
                }

                synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,700,100,300);
                c.fillRect(100,900,300,100);
                c.fillRect(0,0,100,400);
                c.fillRect(0,0,400,100);
                c.fillRect(600,0,400,100);
                c.fillRect(900,100,100,300);
                c.fillRect(900,700,100,300);
                c.fillRect(600,900,300,100);
                c.fillRect(400,400,200,200);

                //draw the spikes
                c.drawImage(spikeUp,400,300);
                c.drawImage(spikeUp,500,300);
                c.drawImage(spikeDown,400,600);
                c.drawImage(spikeDown,500,600);
                c.drawImage(spikeLeft,300,400);
                c.drawImage(spikeLeft,300,500);
                c.drawImage(spikeRight,600,400);
                c.drawImage(spikeRight,600,500);


                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);
                c.setColor(Color.YELLOW);
                  if (eAHealth[8] > 0)
                  {
                    c.drawImage(enemyA, eAX[8], eAY[8], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[8], eAX[8]+25, eAY[8]+65);
                  }
                  if (eAHealth[9] > 0)
                  {
                    c.drawImage(enemyA, eAX[9], eAY[9], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[9], eAX[9]+25, eAY[9]+65);
                  }
                  if (eAHealth[10] > 0)
                  {
                    c.drawImage(enemyA, eAX[10], eAY[10], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[10], eAX[10]+25, eAY[10]+65);
                  }
                  if (eAHealth[11] > 0)
                  {
                    c.drawImage(enemyA, eAX[11], eAY[11], eAWidth, eAHeight);
                    c.drawString(""+eAHealth[11], eAX[11]+25, eAY[11]+65);
                  }

                  //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }

              //draw the additional info
              c.setFont(f1);
              c.setColor(Color.WHITE);
              c.drawString("Damage: " + damage, 100,100);
              c.drawString("MaxHealth:  " + healthMax, 100,150);
              c.drawString("LifeSteal:  " + lifeSteal, 100,200);
                }

                if (distanceLinePoint(400,900,400,1100,jXc,jYc) < width / 2) 
                        {
                            x+= 2;
                        }
                else if (distanceLinePoint(600,900,600,1100,jXc,jYc) < width / 2) 
                        {
                            x-= 2;
                        }
                else if (distanceLinePoint(100,900,400,900,jXc,jYc) < width / 2) 
                        {
                            y-= 2;
                        }
                else if (distanceLinePoint(600,900,900,900,jXc,jYc) < width / 2) 
                        {
                            y-= 2;
                        }
                else if (distanceLinePoint(100,700,100,900,jXc,jYc) < width / 2) 
                        {
                            x+= 2;
                        }
                else if (distanceLinePoint(900,700,900,900,jXc,jYc) < width / 2) 
                        {
                            x-= 2;
                        }
                else if (distanceLinePoint(100,100,100,400,jXc,jYc) < width / 2) 
                        {
                            x+= 2;
                        }
                else if (distanceLinePoint(900,100,900,400,jXc,jYc) < width / 2) 
                        {
                            x-= 2;
                        }
                else if (distanceLinePoint(100,100,400,100,jXc,jYc) < width / 2) 
                        {
                            y+= 2;
                        }
                else if (distanceLinePoint(600,100,900,100,jXc,jYc) < width / 2) 
                        {
                            y+= 2;
                        }
                else if (distanceLinePoint(900,400,1000,400,jXc,jYc) < width / 2) 
                        {
                            y+= 2;
                        }
                else if (distanceLinePoint(-100,400,100,400,jXc,jYc) < width / 2) 
                        {
                            y+= 2;
                        }
                else if (distanceLinePoint(-100,700,100,700,jXc,jYc) < width / 2) 
                        {
                            y-= 2;
                        }
                else if (distanceLinePoint(900,700,1000,700,jXc,jYc) < width / 2) 
                        {
                            y-= 2;
                        }
                else if (distanceLinePoint(-200,400,-200,700,jXc,jYc) < width / 2) //returning to A5
                        {
                            inA5 = true;
                            inA6 = false;
                            levelLock = -7;
                        }
                else if (distanceLinePoint(400,0,600,0,jXc,jYc) < width / 2) //going into A7
                        {
                            inA7 = true;
                            inA6 = false;
                        }
                else if (distanceLinePoint(400,1000,600,1000,jXc,jYc) < width / 2) //going into A8
                        {
                            inA8 = true;
                            inA6 = false;

                        }
                else if (distanceLinePoint(1100,400,1100,700,jXc,jYc) < width / 2) //going into A9
                        {
                            inA9 = true;
                            inA6 = false;
                        }
    //collision with spikes that damage you
    if (distanceLinePoint(400,300,600,300,jXc,jYc) < width / 2) 
                {
                    y-= 100;
                    health -= 20;
                    }
    else if (distanceLinePoint(400,300,400,400,jXc,jYc) < width / 2) 
                    {
                        x-= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(600,300,600,400,jXc,jYc) < width / 2) 
                    {
                        x+= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(300,400,400,400,jXc,jYc) < width / 2) 
                    {
                        y-= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(600,400,700,400,jXc,jYc) < width / 2) 
                    {
                        y-= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(300,400,300,600,jXc,jYc) < width / 2) 
                    {
                        x-= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(400,600,300,600,jXc,jYc) < width / 2) 
                    {
                        y+= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(400,600,400,700,jXc,jYc) < width / 2) 
                    {
                        x-= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(600,700,400,700,jXc,jYc) < width / 2) 
                    {
                        y+= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(600,700,600,600,jXc,jYc) < width / 2) 
                    {
                        x+= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(600,600,700,600,jXc,jYc) < width / 2) 
                    {
                        y+= 100;
                        health -= 20;
                    }
    else if (distanceLinePoint(700,600,700,400,jXc,jYc) < width / 2) 
                    {
                        x+= 100;
                        health -= 20;
                    }
                                     
    //collision with walls for enemyAs
                if (eAY[8] > 875)
                {
                    eAdy[8] = -1;
                }
                else if (eAY[8] < 100)
                {
                    eAdy[8] = 1;
                }

                if(eAY[9] > 875)
                {
                    eAdy[9] = -1;
                }
                else if(eAY[9] < 100)
                {
                    eAdy[9] = 1;
                }

                if (eAY[10] > 675)
                {
                    eAdy[10] = -1;
                }
                else if (eAY[10] < 400)
                {
                    eAdy[10] = 1;
                }

                if(eAY[11] > 875)
                {
                    eAdy[11] = -1;
                }
                else if(eAY[11] < 100)
                {
                    eAdy[11] = 1;
                }

    //collision for jeffard and enemyAs
                if (distance(jXc, jYc, eAXc[8], eAYc[8]) < width && eAHealth[8] > 0)
                {
                    //knockback so that the player is not continuosly damaged
                    if (dir == 0)
                    {
                    x-= 100;
                    }
                    else if (dir == 180)
                    {
                    x+= 100;
                    }
                    else if (dir == 90)
                    {
                    y+= 100;
                    }
                    else if (dir == 270)
                    {
                    y-= 100;
                    }
                    
                    health -= 10;
                }

                if (distance(jXc, jYc, eAXc[9], eAYc[9]) < width && eAHealth[9] > 0)
                {
                    //knockback so that the player is not continuosly damaged
                    
                    if (dir == 0)
                    {
                    x-= 100;
                    }
                    else if (dir == 180)
                    {
                    x+= 100;
                    }
                    else if (dir == 90)
                    {
                    y+= 100;
                    }
                    else if (dir == 270)
                    {
                    y-= 100;
                    }
                    
                    health -= 10;
                }

                if (distance(jXc, jYc, eAXc[10], eAYc[10]) < width && eAHealth[10] > 0)
                {
                    //knockback so that the player is not continuosly damaged
                    if (dir == 0)
                    {
                    x-= 100;
                    }
                    else if (dir == 180)
                    {
                    x+= 100;
                    }
                    else if (dir == 90)
                    {
                    y+= 100;
                    }
                    else if (dir == 270)
                    {
                    y-= 100;
                    }
                    
                    health -= 10;
                }

                if (distance(jXc, jYc, eAXc[11], eAYc[11]) < width && eAHealth[11] > 0)
                {
                    //knockback so that the player is not continuosly damaged
                    
                    if (dir == 0)
                    {
                    x-= 100;
                    }
                    else if (dir == 180)
                    {
                    x+= 100;
                    }
                    else if (dir == 90)
                    {
                    y+= 100;
                    }
                    else if (dir == 270)
                    {
                    y-= 100;
                    }
                    
                    health -= 10;
                }

                //collision for enemyAs and weapon slash

                    if (distance(slashXc,slashYc, eAXc[8], eAYc[8]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[8] > 0)
                    {
                    eAHealth[8] -= damage;
                    health += lifeSteal;
                    }
                    
                    if (distance(slashXc,slashYc, eAXc[9], eAYc[9]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[9] > 0)
                    {
                    eAHealth[9] -= damage;
                    health += lifeSteal;
                    }

                    if (distance(slashXc,slashYc, eAXc[10], eAYc[10]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[10] > 0)
                    {
                    eAHealth[10] -= damage;
                    health += lifeSteal;
                    }
                    
                    if (distance(slashXc,slashYc, eAXc[11], eAYc[11]) < eAWidth && keyChar == 'k' && quarterSecond && eAHealth[11] > 0)
                    {
                    eAHealth[11] -= damage;
                    health += lifeSteal;
                    }

                    if (health > healthMax) // This prevents overheal
                    {
                    health = healthMax;
                    }

                //enemyAs' movement updates
                eAY[8] += eAdy[8];
                eAXc[8] = eAX[8] + eAWidth / 2;
                eAYc[8] = eAY[8] + eAHeight / 2;

                eAY[9] += eAdy[9];
                eAXc[9] = eAX[9] + eAWidth / 2;
                eAYc[9] = eAY[9] + eAHeight / 2;

                eAY[10] += eAdy[10];
                eAXc[10] = eAX[10] + eAWidth / 2;
                eAYc[10] = eAY[10] + eAHeight / 2;

                eAY[11] += eAdy[11];
                eAXc[11] = eAX[11] + eAWidth / 2;
                eAYc[11] = eAY[11] + eAHeight / 2;

    }

    public static void a7Method()
    {

      if (levelLock == 7)
      {
        x = 400;
        y = 900;
        levelLock = 8;
        eBX[3] = (int)(Math.random()*2000); 
        eBY[3] = (int)(Math.random()*2000);
        eBX[4] = (int)(Math.random()*2000); 
        eBY[4] = (int)(Math.random()*2000);

        eBHealth[3] = 100;
        eBHealth[4] = 100;

        boss1X = 500;
        boss1Y = 100;
        boss1dx = 1;
        boss1dy = 0;
        if(chestLock == 3 || chestLock == 2 || chestLock == 1) 
        {
          chestClaimed[3] = false;
          chestX[3] = 400;
          chestY[3] = 100;
          chestXc[3] = chestX[3] + chestWidth / 2;
          chestYc[3] = chestY[3] + chestHeight / 2;
          chestLock = 4;
        }

      }
      
      
      synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,0,1000,100);
                c.fillRect(0,0,100,1000);
                c.fillRect(900,0,100,1000);
                c.fillRect(0,900,400,100);
                c.fillRect(600,900,400,100);

                //draw the chest
                if(chestClaimed[3] == false)
                {
                  c.drawImage(chest,400,100,chestWidth,chestHeight);
                }

                //draw entities 

                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);

                c.setColor(Color.YELLOW);
                if (eBHealth[3] > 0)
                  {
                    c.drawImage(enemyB, eBX[3], eBY[3], eBWidth, eBHeight);
                    c.drawString(""+eBHealth[3], eBX[3]-5, eBY[3]+110);
                  }
                if (eBHealth[4] > 0)
                  {
                    c.drawImage(enemyB, eBX[4], eBY[4], eBWidth, eBHeight);
                    c.drawString(""+eBHealth[4], eBX[4]-5, eBY[4]+110);
                  }

            //draw the boss
            if((boss1dx == 1 || boss1dy == -1)&& miniBoss1Alive)
            {
              c.drawImage(miniBoss1Right, boss1X,boss1Y, boss1Width,boss1Height);
            }
            else if((boss1dx == -1 || boss1dy == 1)&& miniBoss1Alive) 
            {
              c.drawImage(miniBoss1Left, boss1X,boss1Y,boss1Width,boss1Height);
            }
            c.setColor(Color.RED);
            if(miniBoss1Alive)
            {
              c.drawString(""+ boss1Health, boss1Xc, (boss1Yc + boss1Height));
            }

            //draw the bossSlash
            if (!(drawBossSlash && threeSeconds) && miniBoss1Alive)
              {
                  if (boss1dx == 1)
                  {
                    c.drawImage(slashRight, boss1Xc + 100, boss1Yc - 25, slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss1dy == -1)
                  {
                    c.drawImage(slashUp, boss1Xc - 50, boss1Yc - 100,slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss1dx == -1)
                  {
                    c.drawImage(slashLeft, boss1Xc - 200, boss1Yc - 25, slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss1dy == 1)
                  {
                    c.drawImage(slashDown, boss1Xc - 50, boss1Yc + 50, slashWidth * 2, slashHeight * 2);
                  }
                }
//draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }

              //draw the additional info
              c.setFont(f1);
              c.setColor(Color.WHITE);
              c.drawString("Damage: " + damage, 100,100);
              c.drawString("MaxHealth:  " + healthMax, 100,150);
              c.drawString("LifeSteal:  " + lifeSteal, 100,200);
                }

      if(distanceLinePoint(400, 900, 400, 1100, jXc, jYc) < width / 2)
                {
                  x+=2;
                }
      else if(distanceLinePoint(600, 900, 600, 1100, jXc, jYc) < width / 2)
                {
                  x-=2;
                }
      else if(distanceLinePoint(100, 900, 400, 900, jXc, jYc) < width / 2)
                {
                  y-=2;
                }
      else if(distanceLinePoint(600, 900, 900, 900, jXc, jYc) < width / 2)
                {
                  y-=2;
                }
      else if(distanceLinePoint(100, 100, 900, 100, jXc, jYc) < width / 2)
                {
                  y+=2;
                }
      else if(distanceLinePoint(900, 100, 900, 900, jXc, jYc) < width / 2)
                {
                  x-=2;
                }
      else if(distanceLinePoint(100, 100, 100, 900, jXc, jYc) < width / 2)
                {
                  x+=2;
                }
      else if(distanceLinePoint(400, 1100, 600, 1100, jXc, jYc) < width / 2) // return to A6
                {
                  inA6 = true;
                  inA7 = false;
                  levelLock = -8;

                }

    //collsion with chest[3] for jeffard
            if (distanceLinePoint(400,100, 400, 200, jXc, jYc) < width / 2 && chestClaimed[3] == false)
            {
              chestRoll = (int)(Math.random()*2);
              if (chestRoll == 0)
              {
                  healthMax += 20;
              }
              else if (chestRoll == 1)
              {
                  damage += 5;
              }
              chestClaimed[3] = true;
            }
            else if (distanceLinePoint(400,200, 500, 200, jXc, jYc) < width / 2 && chestClaimed[3] == false)
            {
              chestRoll = (int)(Math.random()*2);
              if (chestRoll == 0)
              {
                  healthMax += 20;
              }
              else if (chestRoll == 1)
              {
                  damage += 5;
              }
              chestClaimed[3] = true;
            }
            else if (distanceLinePoint(500,200, 500, 100, jXc, jYc) < width / 2 && chestClaimed[3] == false)
            {
              chestRoll = (int)(Math.random()*2);
              if (chestRoll == 0)
              {
                  healthMax += 20;
              }
              else if (chestRoll == 1)
              {
                  damage += 5;
              }
              chestClaimed[3] = true;
            }

    //collision for jeffard and enemyBs
            if(distance(jXc, jYc, eBXc[3], eBYc[3]) < (width / 2) && eBHealth[3] > 0)
            {
            eBX[3] = (int)(Math.random()*2000);
            eBY[3] = (int)(Math.random()*2000);
            health -= 10;
            }

            if(distance(jXc, jYc, eBXc[4], eBYc[4]) < (width / 2) && eBHealth[4] > 0)
            {
            eBX[4] = (int)(Math.random()*2000);
            eBY[4] = (int)(Math.random()*2000);
            health -= 10;
            }

    //collision for enemyBs and weapon slash
            if (distance(slashXc,slashYc, eBXc[3], eBYc[3]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[3] > 0)
            {
            eBHealth[3] -= damage;
            eBX[3] = (int)(Math.random()*2000);
            eBY[3] = (int)(Math.random()*2000);
            }

            if (distance(slashXc,slashYc, eBXc[4], eBYc[4]) < eBWidth  && keyChar == 'k' && quarterSecond && eBHealth[4] > 0)
            {
            eBHealth[4] -= damage;
            eBX[4] = (int)(Math.random()*2000);
            eBY[4] = (int)(Math.random()*2000);
            }

    //enemyBs' movement updates

            eBXc[3] = eBX[3] + eBWidth / 2;
            eBYc[3] = eBY[3] + eBHeight / 2;
            eBXc[4] = eBX[4] + eBWidth / 2;
            eBYc[4] = eBY[4] + eBHeight / 2;


    //the enemy goes towards the player
            if(eBX[3] < x)
            {
            eBX[3] = eBX[3] + (x - (x-1));  //1
            }
            if(eBX[3] > x)
            {            
            eBX[3] = eBX[3] + (x - (x+1)); //1
            }
            if(eBY[3] < y)
            {
            eBY[3] = eBY[3] + (y - (y-1)); //1
            }
            if(eBY[3] > y)
            {
            eBY[3] = eBY[3] + (y - (y+1)); //1
            }

            if(eBX[4] < x)
            {
            eBX[4] = eBX[4] + (x - (x-1));  //1
            }
            if(eBX[4] > x)
            {            
            eBX[4] = eBX[4] + (x - (x+1)); //1
            }
            if(eBY[4] < y)
            {
            eBY[4] = eBY[4] + (y - (y-1)); //1
            }
            if(eBY[4] > y)
            {
            eBY[4] = eBY[4] + (y - (y+1)); //1
            }

//collisons with walls for boss

if(distanceLinePoint(100, 900, 400, 900, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dy = -1;
  }
else if(distanceLinePoint(600, 900, 900, 900, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dy = -1;
  }
else if(distanceLinePoint(100, 100, 900, 100, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dy = 1;
  }
else if(distanceLinePoint(900, 100, 900, 900, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dx = -1;
  }
else if(distanceLinePoint(100, 100, 100, 900, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dx = 1;
  }
else if(distanceLinePoint(400, 900, 400, 1100, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dx = 1;
  }
else if(distanceLinePoint(600, 900, 600, 1100, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dx = -1;
  }
else if(distanceLinePoint(400, 900, 600, 900, boss1Xc, boss1Yc) < boss1Width / 2)
  {
    boss1dy = -1;
  }
  //somewhat random movement from the boss
  bossDisplacement = (int)(Math.random()*10000);
  if(bossDisplacement < 5)
  {
    switchD = true;
    {
      
      if (boss1dx == 1 || boss1dx == -1)
      {
        boss1dx = 0;
        boss1dy = -1;
      }
      else if (boss1dy == 1 || boss1dy == -1)
      {
        boss1dy = 0;
        boss1dx = -1;
      }
    }
    switchD = false;
  }

  //collision with jeffard and boss
  if (distance(jXc, jYc, boss1Xc, boss1Yc) < width  && miniBoss1Alive)
  {
            if (boss1dx == -1)
                  {
                    x-= 300;
                  }
                  else if (boss1dx == 1)
                  {
                    x+= 300;
                  }
                  else if (boss1dy == 1)
                  {
                    y+= 300;
                  }
                  else if (boss1dy == -1)
                  {
                    y-= 300;
                  }
              
              health-=10;
                  
  }
  
  if(miniBoss1Alive)
  {
    //update the centre of the boss aswell as his slash
    boss1Xc = boss1X + boss1Width / 2;
    boss1Yc = boss1Y + boss1Height / 2;
    
    boss1X += boss1dx;
    boss1Y += boss1dy;

    bossSlashXc = bossSlashX + slashWidth;
    bossSlashYc = bossSlashY + slashHeight;

    //update boss1Slashes position on the map
  
      if (boss1dx == 1)
      {
        bossSlashX = boss1Xc+ 100;  
        bossSlashY = boss1Yc- 25;   
        
      }
      else if (boss1dy == -1 )
      {
        bossSlashX = boss1Xc- 50;
        bossSlashY = boss1Yc- 100;
      }
      else if (boss1dx == -1)
      {
        bossSlashX = boss1Xc- 200;
        bossSlashY = boss1Yc- 25;
      }
      else if (boss1dy == 1)
      {
        bossSlashX = boss1Xc- 50;
        bossSlashY = boss1Yc+ 50;
      }
  
  }
    
  //collision for jeffard and bossSlash
    
      if (distance(bossSlashX,bossSlashYc, jXc, jYc) < width && health > 0)
              {

              if (boss1dx == -1)
                  {
                    x-= 300;
                  }
                  else if (boss1dx == 1)
                  {
                    x+= 300;
                  }
                  else if (boss1dy == 1)
                  {
                    y+= 300;
                  }
                  else if (boss1dy == -1)
                  {
                    y-= 300;
                  }
              
                health -= boss1Damage;
                drawBossSlash = true;
                lastBossAttack = currentTimeMillis;
              }
  //collison with slash and boss

   if (distance(slashXc,slashYc, boss1Xc, boss1Yc) < boss1Width && keyChar == 'k' && quarterSecond && miniBoss1Alive)
              {
                boss1Health -= damage;
              }

  //To make sure the player doesnt go out of bounds

  if(miniBoss1Alive)
  {
    if (x < 100 )
    {
      x = 100; 
    }
    else if (x > 900)
    {
      x = 836;
    }
    else if (y < 100)
    {
      y = 100;
    }
    else if (y > 900)
    {
      y = 836;
    }
  }
  
  if (boss1Health <= 0)
  {
    miniBoss1Alive = false;
  }
    }

    public static void a8Method()
    {
      if (levelLock == 7)
      {
        x = 500;
        y = 100;
        levelLock = 8; //arbitrary number, just so that the levelLock loop doesnt repeat

        if(chestLock == 4 || chestLock == 2 || chestLock == 2 || chestLock == 1) 
        {
            chestClaimed[4] = false;
            chestX[4] = 400;
            chestY[4] = 800;
            chestXc[4] = chestX[4] + chestWidth / 2;
            chestYc[4] = chestY[4] + chestHeight / 2;
            chestLock = 5;
        }
        boss2X = 400;
        boss2Y = 700;
        boss2dx = 1;
        boss2dy = 0;
      }
      
      synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,900,1000,100);
                c.fillRect(0,0,100,1000);
                c.fillRect(900,0,100,1000);
                c.fillRect(0,0,400,100);
                c.fillRect(600,0,400,100);
                c.fillRect(300,300,100,100);
                c.fillRect(600,300,100,100);
                c.fillRect(600,600,100,100);
                c.fillRect(300,600,100,100);

                //draw the chest
                
                if(chestClaimed[4] == false)
                {
                  c.drawImage(chest,400,800,chestWidth,chestHeight);
                }
                
                //draw the entities
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);

                 //draw the boss
                  if((boss2dx == 1 || boss2dy == -1)&& miniBoss2Alive)
                  {
                    c.drawImage(miniBoss2Right, boss2X,boss2Y, boss2Width,boss2Height);
                  }
                  else if((boss2dx == -1 || boss2dy == 1)&& miniBoss2Alive) 
                  {
                    c.drawImage(miniBoss2Left, boss2X,boss2Y,boss2Width,boss2Height);
                  }
                  c.setColor(Color.RED);
                  if(miniBoss2Alive)
                  {
                    c.drawString(""+ boss2Health, boss2Xc, (boss2Yc + boss2Height));
                  }

            //draw the bossSlash
            if (!(drawBossSlash && threeSeconds) && miniBoss2Alive)
              {
                  if (boss2dx == 1)
                  {
                    c.drawImage(slashRight, boss2Xc + 100, boss2Yc - 25, slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss2dy == -1)
                  {
                    c.drawImage(slashUp, boss2Xc - 50, boss2Yc - 100,slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss2dx == -1)
                  {
                    c.drawImage(slashLeft, boss2Xc - 200, boss2Yc - 25, slashWidth * 2, slashHeight * 2);
                  }
                  else if (boss2dy == 1)
                  {
                    c.drawImage(slashDown, boss2Xc - 50, boss2Yc + 50, slashWidth * 2, slashHeight * 2);
                  }
                }

                //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }

              //draw the additional info
              c.setFont(f1);
              c.setColor(Color.WHITE);
              c.drawString("Damage: " + damage, 100,100);
              c.drawString("MaxHealth:  " + healthMax, 100,150);
              c.drawString("LifeSteal:  " + lifeSteal, 100,200);
                }
        
    //wall collisions for jeffard

                    if(distanceLinePoint(400, -100, 400, 100, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }
                    else if(distanceLinePoint(600, -100, 600, 100, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(100, 100, 400, 100, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }
                    else if(distanceLinePoint(600, 100, 900, 100, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }
                    else if(distanceLinePoint(900, 100, 900, 900, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(100, 100, 100, 900, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }
                    else if(distanceLinePoint(100, 900, 900, 900, jXc, jYc) < width / 2)
                              {
                                y-=2;
                              }
                    
                    else if(distanceLinePoint(300, 300, 400, 300, jXc, jYc) < width / 2)
                              {
                                y-=2;
                              }
                    else if(distanceLinePoint(300, 300, 300, 400, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(300, 400, 400, 400, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }
                    else if(distanceLinePoint(400, 400, 400, 300, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }

                    else if(distanceLinePoint(600, 300, 600, 400, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(600, 300, 700, 300, jXc, jYc) < width / 2)
                              {
                                y-=2;
                              }
                    else if(distanceLinePoint(700, 300, 700, 400, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }
                    else if(distanceLinePoint(600, 400, 700, 400, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }

                    else if(distanceLinePoint(700, 300, 700, 400, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }

                    else if(distanceLinePoint(300, 600, 400, 600, jXc, jYc) < width / 2)
                              {
                                y-=2;
                              }
                    else if(distanceLinePoint(300, 700, 300, 600, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(300, 700, 400, 700, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }
                    else if(distanceLinePoint(400, 600, 400, 700, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }
                    
                    else if(distanceLinePoint(600, 600, 600, 700, jXc, jYc) < width / 2)
                              {
                                x-=2;
                              }
                    else if(distanceLinePoint(600, 600, 700, 600, jXc, jYc) < width / 2)
                              {
                                y-=2;
                              }
                    else if(distanceLinePoint(700, 600, 700, 700, jXc, jYc) < width / 2)
                              {
                                x+=2;
                              }
                    else if(distanceLinePoint(600, 700, 750, 700, jXc, jYc) < width / 2)
                              {
                                y+=2;
                              }
      else if(distanceLinePoint(400, -100, 600, -100, jXc, jYc) < width / 2) //return to A6
                {
                  inA6 = true;
                  inA8 = false;
                  levelLock = -9;
                }

      //collsion with chest[4] for jeffard
      if (distanceLinePoint(400,800, 500, 800, jXc, jYc) < width / 2 && chestClaimed[4] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[4] = true;
      }
      else if (distanceLinePoint(400,800, 400, 900, jXc, jYc) < width / 2 && chestClaimed[4] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[4] = true;
      }
      else if (distanceLinePoint(500,800, 500, 900, jXc, jYc) < width / 2 && chestClaimed[4] == false)
      {
        chestRoll = (int)(Math.random()*2);
        if (chestRoll == 0)
        {
            healthMax += 20;
        }
        else if (chestRoll == 1)
        {
            damage += 5;
        }
        chestClaimed[4] = true;
      }

  //collisions with walls for boss
  if(distanceLinePoint(400, -100, 400, 100, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }
                    else if(distanceLinePoint(600, -100, 600, 100, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(100, 100, 400, 100, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }
                    else if(distanceLinePoint(600, 100, 900, 100, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }
                    else if(distanceLinePoint(900, 100, 900, 900, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(100, 100, 100, 900, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }
                    else if(distanceLinePoint(100, 900, 900, 900, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = -1;
                              }
                    
                    else if(distanceLinePoint(300, 300, 400, 300, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = -1;
                              }
                    else if(distanceLinePoint(300, 300, 300, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(300, 400, 400, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }
                    else if(distanceLinePoint(400, 400, 400, 300, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }

                    else if(distanceLinePoint(600, 300, 600, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(600, 300, 700, 300, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = -1;
                              }
                    else if(distanceLinePoint(700, 300, 700, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }
                    else if(distanceLinePoint(600, 400, 700, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }

                    else if(distanceLinePoint(700, 300, 700, 400, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }

                    else if(distanceLinePoint(300, 600, 400, 600, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = -1;
                              }
                    else if(distanceLinePoint(300, 700, 300, 600, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(300, 700, 400, 700, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }
                    else if(distanceLinePoint(400, 600, 400, 700, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }
                    
                    else if(distanceLinePoint(600, 600, 600, 700, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = -1;
                              }
                    else if(distanceLinePoint(600, 600, 700, 600, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = -1;
                              }
                    else if(distanceLinePoint(700, 600, 700, 700, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dx = 1;
                              }
                    else if(distanceLinePoint(600, 700, 750, 700, boss2Xc, boss2Yc) < boss2Width / 2)
                              {
                                boss2dy = 1;
                              }
                    else if(distanceLinePoint(400, 0, 600, 0, boss2Xc, boss2Yc) < boss2Width / 2) 
                                {
                                boss2dy = 1;
                                }

  //somewhat random movement from the boss
  bossDisplacement = (int)(Math.random()*8000);
  if(bossDisplacement < 5)
  {
    switchD = true;
    {
      
      if (boss2dx == 1 || boss2dx == -1)
      {
        boss2dx = 0;
        boss2dy = -1;
      }
      else if (boss2dy == 1 || boss2dy == -1)
      {
        boss2dy = 0;
        boss2dx = -1;
      }
    }
    switchD = false;
  }

  //collision with jeffard and boss
  if (distance(jXc, jYc, boss2Xc, boss2Yc) < width  && miniBoss2Alive)
  {
            if (boss2dx == -1)
                  {
                    x-= 300;
                  }
                  else if (boss2dx == 1)
                  {
                    x+= 300;
                  }
                  else if (boss2dy == 1)
                  {
                    y+= 300;
                  }
                  else if (boss2dy == -1)
                  {
                    y-= 300;
                  }
              
              health -= 20;
                  
  }
  
  if(miniBoss2Alive)
  {
    //update the centre of the boss aswell as his slash
    boss2Xc = boss2X + boss2Width / 2;
    boss2Yc = boss2Y + boss2Height / 2;
    
    boss2X += boss2dx;
    boss2Y += boss2dy;

    bossSlashXc = bossSlashX + slashWidth;
    bossSlashYc = bossSlashY + slashHeight;

    //update boss2Slashes position on the map
  
      if (boss2dx == 1)
      {
        bossSlashX = boss2Xc+ 100;  
        bossSlashY = boss2Yc- 25;   
        
      }
      else if (boss2dy == -1 )
      {
        bossSlashX = boss2Xc- 50;
        bossSlashY = boss2Yc- 100;
      }
      else if (boss2dx == -1)
      {
        bossSlashX = boss2Xc- 200;
        bossSlashY = boss2Yc- 25;
      }
      else if (boss2dy == 1)
      {
        bossSlashX = boss2Xc- 50;
        bossSlashY = boss2Yc+ 50;
      }
  
  }
  

  //collision for jeffard and bossSlash
    
      if (distance(bossSlashX,bossSlashYc, jXc, jYc) < width && health > 0)
              {

              if (boss2dx == -1)
                  {
                    x-= 500;
                  }
                  else if (boss2dx == 1)
                  {
                    x+= 500;
                  }
                  else if (boss2dy == 1)
                  {
                    y+= 500;
                  }
                  else if (boss2dy == -1)
                  {
                    y-= 500;
                  }
              
              health -= 50;
              
                health -= boss2Damage;
                drawBossSlash = true;
                lastBossAttack = currentTimeMillis;
              }
  //collison with slash and boss

   if (distance(slashXc,slashYc, boss2Xc, boss2Yc) < boss2Width && keyChar == 'k' && quarterSecond && miniBoss2Alive)
              {
                boss2Health -= damage;
              }


  //To make sure the player doesnt go out of bounds
  if(miniBoss2Alive)
  {
    if (x < 100 )
    {
      x = 100; 
    }
    else if (x > 900)
    {
      x = 836;
    }
    else if (y < 100)
    {
      y = 100;
    }
    else if (y > 900)
    {
      y = 836;
    }
  }
  
  
  if (boss2Health <= 0)
  {
    miniBoss2Alive = false;
  }




    }

  public static void a9Method() //final area
    {
      
      if (levelLock == 7) 
      {
         x = -50;
         y = 550;
         
         levelLock = 9;  //arbitrary number, so that the levelLock loop doesnt repeat

        boss3X = 700;
        boss3Y = 500;
        boss3dx = 0;
        boss3dy = 2;
      }
     
      
      //somewhat random movement from the boss
      bossDisplacement = (int)(Math.random()*5000);
      if(bossDisplacement < 5)
      {
        switchD = true;
        {
          
          if (boss3dx == 2 || boss3dx == -2)
          {
            boss3dx = 0;
            boss3dy = -2;
          }
          else if (boss3dy == 2 || boss3dy == -2)
          {
            boss3dy = 0;
            boss3dx = -2;
          }
        }
        switchD = false;
      }

      synchronized(c){
                c.setBackgroundColor(Color.GRAY);
                c.clear();
                c.setColor(backgroundColor);
                c.fillRect(0,0,1000,50);
                c.fillRect(0,950,1000,50);
                c.fillRect(950,0,50,1000);
                c.fillRect(0,0,50,400);
                c.fillRect(0,700,50,400);
                c.fillRect(500,200,100,100);
                c.fillRect(500,700,100,100);
                if(doorShut)
                {
                  c.fillRect(0,400,50,300);
                }
                
           //draw the boss
                if(boss3dx == 2 || boss3dy == -2)
                {
                  c.drawImage(finalBossRight, boss3X,boss3Y);
                }
                else if(boss3dx == -2 || boss3dy == 2) 
                {
                  c.drawImage(finalBossLeft, boss3X,boss3Y);
                }
                c.setColor(Color.RED);
                c.drawString(""+ boss3Health, boss3Xc, (boss3Yc + boss3Height));
               
            //draw the spikes
                c.drawImage(spikeLeft, 400,200);
                c.drawImage(spikeRight, 600,200);
                c.drawImage(spikeDown,500,300);
                c.drawImage(spikeUp,500,600);
                c.drawImage(spikeLeft, 400,700);
                c.drawImage(spikeRight, 600,700);
                
                
                if (miniBoss1Alive == false && miniBoss2Alive == false)
                 {
                   if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardFullRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardFullLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss1Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardShieldRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardShieldLeft, x, y, width, height); 
                  }
                 }
                 else if(miniBoss2Alive == false)
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardAxeRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardAxeLeft, x, y, width, height); 
                  }
                 }
                 else 
                 {
                  if (dir == 0 || dir == 90) 
                  {
                      c.drawImage(jeffardRight, x, y, width, height); 
                  } 
                  else if (dir == 180 || dir == 270) 
                  {
                      c.drawImage(jeffardLeft, x, y, width, height); 
                  }
                 }
                c.setColor(Color.GREEN);
                c.drawString(""+health, x + 16, y +96);

        //draw the bossSlash
        if (!(drawBossSlash && threeSeconds))
                  {
                      if (boss3dx == 2)
                      {
                        c.drawImage(slashRight, boss3Xc + 100, boss3Yc - 25, slashWidth * 2, slashHeight * 2);
                      }
                      else if (boss3dy == -2)
                      {
                        c.drawImage(slashUp, boss3Xc - 50, boss3Yc - 100,slashWidth * 2, slashHeight * 2);
                      }
                      else if (boss3dx == -2)
                      {
                        c.drawImage(slashLeft, boss3Xc - 200, boss3Yc - 25, slashWidth * 2, slashHeight * 2);
                      }
                      else if (boss3dy == 2)
                      {
                        c.drawImage(slashDown, boss3Xc - 50, boss3Yc + 50, slashWidth * 2, slashHeight * 2);
                      }
                    }


           //draw the weapon slash
                  if (!(drawSlash && quarterSecond))
                  {
                      if (dir == 0)
                      {
                        c.drawImage(slashRight, jXc + 50, jYc - 25, 60, 60);
                      }
                      else if (dir == 90)
                      {
                        c.drawImage(slashUp, jXc - 25, jYc - 100, 60, 60);
                      }
                      else if (dir == 180)
                      {
                        c.drawImage(slashLeft, jXc - 100, jYc - 25, 60, 60);
                      }
                      else if (dir == 270)
                      {
                        c.drawImage(slashDown, jXc - 25, jYc + 50, 60, 60);
                      }
                    }                }
       //collision for jeffard
     
                if(distanceLinePoint(200,400,200,700, jXc, jYc) < width / 2) 
                          {
                          doorShut = true;
                          }
                else if(distanceLinePoint(50,50,50,950, jXc, jYc) < width / 2) 
                            {
                              x+=2;
                            }
                else if(distanceLinePoint(0, 400, 50, 400, jXc, jYc) < width / 2)
                          {
                            y+=2;
                          }
                else if(distanceLinePoint(0, 700, 50, 700, jXc, jYc) < width / 2)
                          {
                            y-=2;
                          }
                else if(distanceLinePoint(50, 50, 950, 50, jXc, jYc) < width / 2)
                          {
                            y+=2;
                          }
                else if(distanceLinePoint(50, 950, 950, 950, jXc, jYc) < width / 2)
                          {
                            y-=2;
                          }
                else if(distanceLinePoint(950, 50, 950, 950, jXc, jYc) < width / 2)
                          {
                            x-=2;
                          }

                else if(distanceLinePoint(500, 200, 600, 200, jXc, jYc) < width / 2)
                          {
                            y-=2;
                          }

                else if(distanceLinePoint(400, 200, 500, 200, jXc, jYc) < width / 2)
                          {
                            y-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(600, 200, 700, 200, jXc, jYc) < width / 2)
                          {
                            y-=100;
                            health-=20;
                          }
                 else if(distanceLinePoint(400, 200, 400, 300, jXc, jYc) < width / 2)
                          {
                            x-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(400, 300, 500, 300, jXc, jYc) < width / 2)
                          {
                            y+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(500, 300, 500, 400, jXc, jYc) < width / 2)
                          {
                            x-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(500, 400, 600, 400, jXc, jYc) < width / 2)
                          {
                            y+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(600, 300, 600, 400, jXc, jYc) < width / 2)
                          {
                            x+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(600, 300, 700, 300, jXc, jYc) < width / 2)
                          {
                            y+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(700, 200, 700, 300, jXc, jYc) < width / 2)
                          {
                            x+=100;
                            health-=20;
                          }
                

                else if(distanceLinePoint(500, 600, 600, 600, jXc, jYc) < width / 2)
                          {
                            y-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(500, 700, 500, 600, jXc, jYc) < width / 2)
                          {
                            x-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(500, 700, 400, 700, jXc, jYc) < width / 2)
                          {
                            y-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(400, 700, 400, 800, jXc, jYc) < width / 2)
                          {
                            x-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(400, 800, 500, 800, jXc, jYc) < width / 2)
                          {
                            y+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(500, 800, 600, 800, jXc, jYc) < width / 2)
                          {
                            y+=2;
                          }
                else if(distanceLinePoint(600, 800, 700, 800, jXc, jYc) < width / 2)
                          {
                            y+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(700, 700, 700, 800, jXc, jYc) < width / 2)
                          {
                            x+=100;
                            health-=20;
                          }
                else if(distanceLinePoint(600, 700, 700, 700, jXc, jYc) < width / 2)
                          {
                            y-=100;
                            health-=20;
                          }
                else if(distanceLinePoint(600, 600, 600, 700, jXc, jYc) < width / 2)
                          {
                            x+=100;
                            health-=20;
                          }
                
                  
      else if(distanceLinePoint(-100, 400, -100, 700, jXc, jYc) < width / 2) //returning to A6
                {
                  inA6 = true;
                  inA9 = false;
                  levelLock = -10;
                }
      

      //collision with jeffard and final boss 

      
      if (distance(jXc, jYc, boss3Xc, boss3Yc) < width  && finalBossAlive)
      {
                  if (dir == 0)
                  {
                    x-= 300;
                  }
                  else if (dir == 180)
                  {
                    x+= 300;
                  }
                  else if (dir == 90)
                  {
                    y+= 300;
                  }
                  else if (dir == 270)
                  {
                    y-= 300;
                  }
                  
                  health -= 20;
      }

      boss3Xc = boss3X + boss3Width / 2;
      boss3Yc = boss3Y + boss3Height / 2;
      
      boss3X += boss3dx;
      boss3Y += boss3dy;

      bossSlashXc = bossSlashX + slashWidth;
      bossSlashYc = bossSlashY + slashHeight;

      //update boss3Slashes position on the map

      
      if (boss3dx == 2)
      {
        bossSlashX = boss3Xc+ 100;  
        bossSlashY = boss3Yc- 25;   
        
      }
      else if (boss3dy == -2 )
      {
        bossSlashX = boss3Xc- 50;
        bossSlashY = boss3Yc- 100;
      }
      else if (boss3dx == -2)
      {
        bossSlashX = boss3Xc- 200;
        bossSlashY = boss3Yc- 25;
      }
      else if (boss3dy == 2)
      {
        bossSlashX = boss3Xc- 50;
        bossSlashY = boss3Yc+ 50;
      }
      
            //collision for jeffard and bossSlash
              
                if (distance(bossSlashX,bossSlashYc, jXc, jYc) < width && health > 0)
                        {

                        if (boss3dx == -2)
                        {
                          x-= 500;
                        }
                        else if (boss3dx == 2)
                        {
                          x+= 500;
                        }
                        else if (boss3dy == 2)
                        {
                          y+= 500;
                        }
                        else if (boss3dy == -2)
                        {
                          y-= 500;
                        }
                        
                          health -= boss3Damage;
                          drawBossSlash = true;
                          lastBossAttack = currentTimeMillis;
                        }
            //collison with slash and finalBoss

            if (distance(slashXc,slashYc, boss3Xc, boss3Yc) < boss3Width && keyChar == 'k' && quarterSecond && finalBossAlive)
                        {
                          boss3Health -= damage;
                        }


            //collision for finalBoss with walls
              
      if(distanceLinePoint(50,50,50,950, boss3Xc, boss3Yc) < boss3Width / 2) 
            {
              boss3dx =2;
            }
      else if(distanceLinePoint(0, 400, 50, 400, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =2;
          }
      else if(distanceLinePoint(0, 700, 50, 700, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =-2;
          }
      else if(distanceLinePoint(50, 50, 950, 50, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =2;
          }
      else if(distanceLinePoint(50, 950, 950, 950, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =-2;
          }
      else if(distanceLinePoint(950, 50, 950, 950, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dx =-2;
          }

      else if(distanceLinePoint(500, 200, 600, 200, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =-2;
          }
      else if(distanceLinePoint(600, 200, 600, 300, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dx =2;
          }
      else if(distanceLinePoint(500, 300, 600, 300, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =2;
          }
      else if(distanceLinePoint(500, 200, 500, 300, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dx =-2;
          }
      else if(distanceLinePoint(500, 800, 600, 800, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =2;
          }
      else if(distanceLinePoint(500, 700, 600, 700, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dy =-2;
          }
      else if(distanceLinePoint(600, 700, 600, 800, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dx =2;
          }
      else if(distanceLinePoint(500, 700, 500, 800, boss3Xc, boss3Yc) < boss3Width / 2)
          {
            boss3dx =-2;
          }
        

        if (boss3Health <= 0)
        {
                      
                alive = false;
                 inGame = false;
                 inA1 = true; 
                 levelLock = 0;
                 chestLock = 0;
                 boss1Lock = 0;
                 boss2Lock = 0;
                 miniBoss1Alive = true;
                 boss1Health = boss1Health;
                 boss2Health = boss2Health;
                 boss3Health = boss3Health;
                 miniBoss2Alive = true;
                 health = 150; 
                 healthMax = 150;
                 damage = 20;
                 c.clear();
                 c.setColor(Color.BLACK);
                 c.drawString("YOU WIN!!", 200,400);
                 delay(2000);
                 chestClaimed[0] = false;
                    chestX[0] = 800;
                    chestY[0] = 100;
                    chestXc[0] = chestX[0] + chestWidth / 2;
                    chestYc[0] = chestY[0] + chestHeight / 2;
                    chestLock = 1;
        }

                if(finalBossAlive)
                  {
                    if (x < 100 )
                    {
                      x = 100; 
                    }
                    else if (x > 950)
                    {
                      x = 886;
                    }
                    else if (y < 100)
                    {
                      y = 100;
                    }
                    else if (y > 900)
                    {
                      y = 836;
                    }
                  }
    
    }
    
 
  //Pre-written methods


  public static Image loadImage(String path){
      Image img = null;
      try{
         img = javax.imageio.ImageIO.read(new java.io.File(path));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return img;
   }


  // determines the distance between point and line
  // int xa, ya  the starting point x1, y1 pair of the line
  // int xb, yb  the end point x2, y2 pair of the line
  // int xc, yc  the point
  public static double distanceLinePoint(int xa, int ya, int xb, int yb, int xc, int yc) {
    int xdiff = xb - xa;
    int ydiff = yb - ya;
    long l2 = (long) (xdiff * xdiff + ydiff * ydiff);
    if (l2 == 0L) {
      return (double) distance(xa, ya, xc, yc);
    }
    else {
      double rnum = (double) ((ya - yc) * (ya - yb) - (xa - xc) * (xb - xa));
      double r = rnum / (double) l2;
      if (r >= 0.0D && r <= 1.0D) {
        double xi = (double) xa + r * (double) xdiff;
        double yi = (double) ya + r * (double) ydiff;
        double xd = (double) xc - xi;
        double yd = (double) yc - yi;
        return Math.sqrt(xd * xd + yd * yd);
      }
      else {
        return 1.7976931348623157E308D;
      }
    }    
  }


  public static long distance(int x1, int y1, int x2, int y2) {
    return (long) Math.sqrt((double) distance2(x1, y1, x2, y2));
  }
 
  public static double distance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(distance2(x1, y1, x2, y2));
  }
 
  public static double distance(java.awt.geom.Point2D.Double p1, java.awt.geom.Point2D.Double p2) {
    return Math.sqrt(distance2(p1.x, p1.y, p2.x, p2.y));
  }
 
  public static long distance2(int x1, int y1, int x2, int y2) {
    return (long) ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }
 
  public static double distance2(double x1, double y1, double x2, double y2) {
    return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
  }


   //this method delays the program by milli milliseconds
  public static void delay(int milli){
    try{
      Thread.sleep(milli);
    }
    catch(Exception e){}
  }


}