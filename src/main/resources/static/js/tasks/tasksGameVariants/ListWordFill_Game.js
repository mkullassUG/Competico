/*TODO
  (raczej nie użyje jquery-ui selectable, zwyczajne buttony onclick wystarczą)
*/
//ListWordFill
const ListWordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
  
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
  
        //NEW 2021-03-08
        console.log(taskData)
        self.answerCurrentlyAt = [];//teraz to będzie tablica obiektów trzymających zaznaczoną odpowiedź
        //TYLKO to sięnie zgrywa z tym co jest w reset TaskVariant, tam jest nadal obiekt
        //to moge zamiast tablicy obiekt trzymający indexy, ex
  
        // self.taskData = taskData;
        self.textField = taskData.text; //tablica tablic zdań
        self.words = taskData.possibleAnswers;//tablica tablic słów
        self.emptySpaceCount = taskData.emptySpaceCount; //teraz to jest tablica liczb (ale nie miała byc jedna odpowiedz na zdanie max?) O.o
        self.startWithText = taskData.startWithText; //tablica boolean
  
        //dla każdego zdania:
        var rows = self.words.length;
  
        $("#GameDiv").html(``);
  
        for (let i = 0; i < rows; i++) {
            var textField = self.textField[i];
            var words = self.words[i];
            var emptySpaceCount = self.emptySpaceCount[i];
            var startWithText = self.startWithText[i];
  
            //1.1 miejsce na index zdania 
            console.log("zdanie nr: " + i);
  
            //1.2 ustawić miejsce na tekst
            var taskContentReady = "("+ (i+1) +"). ";
            var howManyBlanksFound = 0;
            if (startWithText) {
                for (let i = 0; i < textField.length; i++) {
                    taskContentReady += textField[i] + ((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answerLWF">[blank]</div>`);
                    howManyBlanksFound++;
                }
            } else {
                for (let i = 0; i < textField.length; i++) {
                    taskContentReady += ((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answerLWF">[blank]</div>`) + textField[i];
                    console.log(howManyBlanksFound);
                    howManyBlanksFound++;
                    
                }
            } 
  
            //2.1 ustawić miejsce na odpowiedzi
            var taskAnswerHolderReady = 
            `<div class="pb-2 mb-0 text-center" id="taskAnswerHolder">`;
            for (let i = 0; i < words.length; i++) {
              taskAnswerHolderReady += 
              `<div class="answerLWF">` + words[i] + `</div>`
            }
            taskAnswerHolderReady += `</div>`;
  
  
            $("#GameDiv").append(`
            <div id="taskContent" class="border-top border-gray">`+ taskContentReady +`</div>
            <div class="mb-0 border-bottom border-gray" id="taskAnswerHolder">
              ` + taskAnswerHolderReady + `
            </div>`)
  
            //2.2 przypisać listenery pod nade odpowiedzi i miejsce na ostateczną odpowiedź
            //Jquery-UI selectable (grupa tego zdania)
  
            //3. oddzielić zdanie od nastepnego
            
            //nie musze ustawiac szerokości holdera bo robie SELECTABLE a nie DRAGGABLE
            //https://api.jqueryui.com/selectable/
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];
  
        return {"answers": answers};
    }
  
    var resetSuper = self.reset;
    self.reset = () => {
        resetSuper();
  
        self.taskVariantInit(taskData);
    }
  
    self.taskVariantInit(taskData);
    return self;
}