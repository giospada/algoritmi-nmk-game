
// https://editor.p5js.org/


let boardSize = 5;
let xMosse = [
  4,4,4,4,4,
  0,0,0,0,0,
  1,1,1,1,1,
  2,2,2,2,2,
  3,3,3,3,3,
];
let yMosse  = [
  0,1,2,3,4,
  0,1,2,3,4,
  0,1,2,3,4,
  0,1,2,3,4,
  0,1,2,3,4,
] ;
let lenMosse = 25;


let currentMove = 0;
let nextMove = 0; 
let reset = false;
let stop = true ;

let firstC;
let secondC;
let clearC;
let padding = 2;
let famePerSecondStandard = 10;
let famePerSecondSimulation = 1;

function setup(){
  createCanvas(1000, 1000);
  background(255);
  stroke(0);
  firstC = color(0, 255, 0);
  secondC = color(255, 0, 0);
  clearC = color(255, 255, 255);
  d = width/boardSize;
  for ( i = 0; i < boardSize; i += 1) { 
    line(d*(i+1),1,d*(i+1),width);  
    line(1,d*(i+1),width,d*(i+1));  
  }
  noStroke();
}

function draw(){
  if(reset){
    reset = false;
    nextMove = -1;
    for(; currentMove > nextMove ;  currentMove--){
      cancellaMossa(xMosse[currentMove],yMosse[currentMove]);
    }
  }else if(!stop){
    if(nextMove<lenMosse){
      nextMove++;
      console.log("Simulation:mossa "+ nextMove +" su "+ lenMosse)
    }else{
      frameRate(famePerSecondStandard)
      stop = true;
      console.log("Stop:Simulation");
    }
  }
  for(; currentMove < nextMove ;  currentMove++){
    aggiungiMossa(xMosse[currentMove],yMosse[currentMove]);
  }
  for(; currentMove > nextMove ;  currentMove--){
    cancellaMossa(xMosse[currentMove],yMosse[currentMove]);
  }
}

function aggiungiMossa(i,j){
  d = width/boardSize;
  if(currentMove%2 == 1)
   fill(firstC);
  else
   fill(secondC);
  rect(i*d + 2 , j *d +2 , d  -4,d  -4);
}

function cancellaMossa(i,j){
  d = width/boardSize;
  fill(clearC);
  rect(i*d  + 1 , j *d  + 1 , d - 2 ,d  - 2);
}

function keyPressed() {
  
  if(key == ' '){
    if(!stop){
      frameRate(famePerSecondStandard);
      console.log("Stop Simulation");
    }else{
      famePerSecondSimulation = 1;
      frameRate(famePerSecondSimulation);
      console.log("Start Simulation");  
    }
    stop = !stop;
    
  }else if(!stop){
    if(key == 'p'){
      famePerSecondSimulation++;
      frameRate(famePerSecondSimulation); 
    }else if(key == 'm'){
      if(famePerSecondSimulation>1)
        famePerSecondSimulation--;
      frameRate(famePerSecondSimulation);
    }
  }else {
    if(key == 'r'){
    reset = true;
    }else if(key == 'a'){
      if(nextMove<lenMosse){
        nextMove++;
        console.log("mossa "+ nextMove +" su "+ lenMosse)
      }
    }else if(key == 'd'){    
      if(nextMove>=0){
        nextMove--;
        console.log("mossa "+ nextMove +" su "+ lenMosse)
      }
    }
  }
}




