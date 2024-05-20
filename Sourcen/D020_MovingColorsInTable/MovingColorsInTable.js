const greenCell = document.querySelector('.green');
const yellowCell = document.querySelector('.yellow');
const redCell = document.querySelector('.red');

idxY = 1;
idxG = 1;
idxR = 1;

const moveGreen = document.querySelector('#move-green');
moveGreen.addEventListener('click', () => {
     setCellColor(0,idxG + 1,"");
     idxG = (idxG + 1) % 3;
     setCellColor(0,idxG + 1,"green");
});

const moveYellow = document.querySelector('#move-yellow');
moveYellow.addEventListener('click', () => {
    setCellColor(1,idxY + 1,"");
    idxY = (idxY + 1) % 3;
    setCellColor(1,idxY + 1,"yellow");
});

const moveRed = document.querySelector('#move-red');
moveRed.addEventListener('click', () => {
    setCellColor(2,idxR + 1,"");
    idxR = (idxR + 1) % 3;
    setCellColor(2,idxR + 1,"red");
});

function setCellColor(colIdx, rowIdx, color){
    const table = document.querySelector('#colorTable');
    const rows  = table.rows;
    const row = rows[rowIdx];
    const cells = row.cells;
    c = cells[colIdx];
    c.style.backgroundColor = color
    // table.rows[row].cells[col].style.backgroundColor(color);
 }

 function delClass(col, row, cl){
 }

 /*
  for (const row of table.rows) {
    for (const cell of row.cells) {
      // Do something with the cell.
      console.log(cell.innerText);
    }
  }
*/
