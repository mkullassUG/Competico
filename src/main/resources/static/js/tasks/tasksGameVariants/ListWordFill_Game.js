/*TODO
  WAŻNE 2021-03-20:

  detekcja czy przycisk jest niżej od gaedivz żeby nei zasłaniać:
  
    console.log(
        "is botton top ("
            + $("#btnNextTask").offset().top + 
        "px) lower than gamediv bottom ("+($("#GameDiv").offset().top + $("#GameDiv").height())+"px + 60 margin): " + (
        ($("#btnNextTask").offset().top) >
        ($("#GameDiv").offset().top + $("#GameDiv").height() + 60)
        )
    )
*/
//ListWordFill
const ListWordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    
    self.singleWordFillArray = [];

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
  
        //NEW 2021-03-08
        self.answerCurrentlyAt = [];//teraz to będzie tablica obiektów trzymających zaznaczoną odpowiedź
        //TYLKO to sięnie zgrywa z tym co jest w reset TaskVariant, tam jest nadal obiekt
        //to moge zamiast tablicy obiekt trzymający indexy, ex

        // self.taskData = taskData;
        self.textField = taskData.text; //tablica tablic zdań
        self.words = taskData.possibleAnswers;//tablica tablic słów
        self.emptySpaceCount = taskData.emptySpaceCount; //teraz to jest tablica liczb (ale nie miała byc jedna odpowiedz na zdanie max?) O.o
        self.startWithText = taskData.startWithText; //tablica boolean
        //Trzeba na końcu taska to zaznaczać na true; (w TaskGameCore)
        self.isTaskDone = false; //żeby pozbyć się event listenera z document, odznaczjącego wybrane odpowiedzi 
        //dla każdego zdania:
        var rows = self.words.length;
  
        $("#GameDiv").html(``);
  
        for (let i = 0; i < rows; i++) {

            var textField = self.textField[i];
            var words = self.words[i];
            var emptySpaceCount = self.emptySpaceCount[i];
            var startWithText = self.startWithText[i];
  
            //1.1 miejsce na index zdania 
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
                    howManyBlanksFound++;
                }
            }

            //2.1 ustawić miejsce na odpowiedzi
            var taskAnswerHolderReady = ``;
            for (let i = 0; i < words.length; i++) {
                taskAnswerHolderReady += 
                `<div class="answerLWF">` + words[i] + `</div>`;
            }

            $("#GameDiv").append(`
            <div class="singleContentAnswerContainer">
                <div class="border-top border-gray taskContent">`+ taskContentReady +`</div>
                <div class="pb-2 mb-0 text-center mb-0 border-bottom border-gray taskAnswerHolder">
                    ` + taskAnswerHolderReady + `
                </div>
            </div>`)
            
        }

        var BlankContainer = (blank_, wordFillContainer_) => {
            var that = {};

            that.name = "BlankContainer";
            that.blankDiv;
            that.selected; //klik
            that.used;  //po kliku, umieszczenie w blanku
            that.text;

            that.init = (blank) => {

                that.blankDiv = $(blank);
                that.selected = false;
                that.used = false;
                that.text = $(blank).text();
                that.mainWordFillContainer = wordFillContainer_;

                that.blankDiv.on('click', (e) => {
                    if (!that.selected)
                        //that.select();
                        that.mainWordFillContainer.blankWasSelected(that);
                    else
                        that.unselect();
                })
            }

            that.unselect = () => {
                that.selected = false;
                that.blankDiv.removeClass("answerLWFselected");
            }

            that.select = () => {
                that.selected = true;
                that.blankDiv.addClass("answerLWFselected");
            }

            that.setBlankAnswerTo = (answer) => {
                
                //set used BUT if different answer was used FOR THIS BLANK then change it to not used
                that.mainWordFillContainer.checkIfAnyAnswerWasUsedForThisBlankAndChangeToNotUsed(that);

                that.text = answer.text;
                that.blankDiv.text(answer.text);
                that.use()
                answer.use();
                that.unselect()
                answer.unselect();
            }

            that.use = () => {
                that.used = true;
                that.blankDiv.addClass("bg-primary").addClass("text-white");
            }

            that.unuse = () => {
                that.used = false;
                that.blankDiv.removeClass("bg-primary").removeClass("text-white");
            }

            that.init(blank_);

            return that;
        }

        var AnswerContainer = (answer_, wordFillContainer_) => {
            var that = {};
            that.name = "AnswerContainer";
            that.answerDiv;
            that.selected; //klik
            that.used;  //po kliku, umieszczenie w blanku
            that.text;
            that.mainWordFillContainer = wordFillContainer_;
            
            that.init = (answer) => {

                that.answerDiv = $(answer);
                that.selected = false;
                that.used = false;
                that.text = $(answer).text();

                that.answerDiv.on('click', (e) => {
                    if (!that.selected) {
                        that.mainWordFillContainer.answerWasSelected(that);
                    } else
                        that.unselect();
                })
            }

            that.unselect = () => {
                that.selected = false;
                that.answerDiv.removeClass("answerLWFselected");
            }

            that.select = () => {
                that.selected = true;
                that.answerDiv.addClass("answerLWFselected");
            }

            that.use = () => {
                that.used = true;
                that.answerDiv.addClass("bg-primary").addClass("text-white");
            }

            that.unuse = () => {
                that.used = false;
                that.answerDiv.removeClass("bg-primary").removeClass("text-white");
            }

            that.init(answer_);

            return that;
        }

        var WordFillContainer = (containerDiv_) => {
            var that = {};
            that.answerContainers = [];
            that.blankContainers = [];
            that.selectedElement;
            //TODO: put answer and blankj containers inside??

            that.init = (containerDiv_) => {
                that.blankElements = $($(containerDiv_).find(".taskContent")[0]).find(".answerLWF");
                that.answerElements = $($(containerDiv_).find(".taskAnswerHolder")[0]).find(".answerLWF");

                for ( let i = 0; i < that.answerElements.length; i++) {
                    var answer = that.answerElements[i];
                    that.answerContainers.push(AnswerContainer(answer, that));
                }

                for ( let i = 0; i < that.blankElements.length; i++) {
                    var blank = that.blankElements[i];
                    that.blankContainers.push(BlankContainer(blank, that));
                }

                var onclickFunction = (e) => {
                    //jeśli niebył click na answer W tym Wordfill Divie to odznacz wszystkie
                    if (self.isTaskDone) {
                        $(document).off('click', onclickFunction);
                        return;
                    }

                    var clickedElement = $(e.target);

                    var wasClickOnAnswerElement = false; //ale wewnątrz danego WordFillLista

                    for ( let i = 0; i < that.blankElements.length; i++) {
                        var blank = that.blankElements[i];
                        if (clickedElement.is(blank)) {
                            wasClickOnAnswerElement = true;
                            return;
                        }
                    }

                    for ( let i = 0; i < that.answerElements.length; i++) {
                        var answer = that.answerElements[i];
                        if (clickedElement.is(answer)){
                            wasClickOnAnswerElement = true;
                            return;
                        }
                    }

                    if (!wasClickOnAnswerElement) {
                        that.unselectAllAnswers();
                        that.unselectAllBlanks();
                    }
                }

                $(document).on('click', onclickFunction);
            }

            that.unselectAllAnswers = () => {

                for (let i = 0; i < that.answerContainers.length; i++) {
                    var answer = that.answerContainers[i];
                    answer.unselect();
                }
            }

            that.unselectAllBlanks = () => {

                for (let i = 0; i < that.blankContainers.length; i++) {
                    var blank = that.blankContainers[i];
                    blank.unselect();
                }
            }

            that.answerWasSelected = (answer) => {

                if ( that.blankContainers.length == 1) {
                    var onlyBlank = that.blankContainers[0];
                    onlyBlank.setBlankAnswerTo(answer);
                } else if ( that.selectedElement != undefined && that.selectedElement.name === "BlankContainer") {
                    that.selectedElement.setBlankAnswerTo(answer);
                } else {
                    that.unselectAllAnswers();
                    answer.select();
                }

            }

            that.blankWasSelected = (blank) => {

                if ( that.selectedElement != undefined && that.selectedElement.name === "AnswerContainer") {
                    blank.setBlankAnswerTo(that.selectedElement);
                } else {
                    that.selectedElement = blank;
                    that.unselectAllBlanks();
                    blank.select();
                }
            }

            that.checkIfAnyAnswerWasUsedForThisBlankAndChangeToNotUsed = (blank) => {
                
                if (blank.text === "" || blank.text === undefined)
                    return;

                for (let i = 0; i < that.answerContainers.length; i++) {
                    var answer = that.answerContainers[i];
                    if ( answer.text === blank.text) {
                        answer.unuse();
                        break;
                    }
                }
            }

            that.init(containerDiv_);
            return that;
        }

        var containers = $(".singleContentAnswerContainer");
        for (let i = 0; i < containers.length; i++) {
            var containerDiv = containers[i];
            self.singleWordFillArray.push( WordFillContainer(containerDiv) );
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];
        for (let i = 0; i < self.singleWordFillArray.length; i++) {
            var wfContainer = self.singleWordFillArray[i];
            answers[i] = [];

            for ( let j = 0; j < wfContainer.blankContainers.length; j++) {
                var filledBlank = wfContainer.blankContainers[j];

                answers[i][j] = filledBlank.text;
            }
        }
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