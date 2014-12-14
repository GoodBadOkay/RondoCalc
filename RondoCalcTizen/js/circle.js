/**
 * Apply a class to each child
 * Required for IE8-
 */

var currentNum = "";

var currentMem = 0.0;
var prevNum = 0.0;
var dotAdded = false;
/*
current action for calcs
0 - default
1 - plus
2 - minus
3 - miltiply
4 - devide
*/
var currentAction = 0;

function addToLabel(a) {
  if (currentNum == 0) {
    currentNum = "" + a;
  } else {
    currentNum += "" + a;
  }  document.getElementById("serverTime").innerHTML = currentNum;
}

function addDotToLabel() {
  if (!dotAdded) {
    addToLabel('.');
    dotAdded = true;
  }
}


function fullReset() {
  resetNum();
  
  currentAction = 0;
}

function resetNum() {
  resetElements();
  
  currentNum = "";
}

function resetElements () {
 document.getElementById("serverTime").innerHTML = "0";
    dotAdded = false;
}


function plus () {
  currentAction = 1;
  fixateNum();
}

function minus () {
  currentAction = 2;
  fixateNum();
}

function devide () {
  currentAction = 4;
  fixateNum();
}

function multiply () {
  currentAction = 3;
  fixateNum();
}

function memRead () {
  currentNum = currentMem;
 document.getElementById("serverTime").innerHTML = currentNum;
}

function memSet () {
   currentMem = currentNum;
}

function fixateNum () {
   prevNum = parseFloat(currentNum);
   resetNum();
}

function calculateNow () {
  switch(currentAction) {
    case 1:
      prevNum = prevNum + parseFloat(currentNum);
 document.getElementById("serverTime").innerHTML = prevNum;
      currentNum
      currentNum = "0";
    case 2:
      prevNum = prevNum -  parseFloat(currentNum);
 document.getElementById("serverTime").innerHTML = prevNum;
      currentNum
      currentNum = "0";
      break;
    case 3:
      prevNum = parseFloat(currentNum) * prevNum;
 document.getElementById("serverTime").innerHTML = prevNum;
      currentNum
      currentNum = "0";
      break;
      break;
    case 4:
      prevNum = prevNum / parseFloat(currentNum);
 document.getElementById("serverTime").innerHTML = prevNum;
      currentNum
      currentNum = "0";
      break;
      break;
    default:  
      break; 
  }
}