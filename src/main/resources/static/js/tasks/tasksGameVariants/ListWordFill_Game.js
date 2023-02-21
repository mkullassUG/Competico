const ListWordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "ListWordFill";
    
    self.singleWordFillArray = [];
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
        self.answerCurrentlyAt = [];
        self.textField = taskData.text;
        self.words = taskData.possibleAnswers;
        self.emptySpaceCount = taskData.emptySpaceCount;
        self.startWithText = taskData.startWithText;
        self.isTaskDone = false

        $("#GameDiv").html(``);
  
        var rows = self.words.length;
        for (let i = 0; i < rows; i++) {

            var textField = self.textField[i];
            var words = self.words[i];
            var emptySpaceCount = self.emptySpaceCount[i];
            var startWithText = self.startWithText[i];
            var taskContentReady = $(`<div class="text-left border-top border-gray taskContent">` + ((rows>1)? `(` + (i+1) + `). ` : "") + `</div>`);

            var singleContentAnswerContainer = $(`<div class="singleContentAnswerContainer">`);

            var howManyBlanksFound = 0;
            if (startWithText) {
                for (let i = 0; i < textField.length; i++) {
                    var textFieldNode = document.createTextNode(textField[i]);

                    taskContentReady.append(textFieldNode);
                    taskContentReady.append(((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answer`+ self.taskName + `">&emsp;&emsp;&emsp;&emsp;</div>`));

                    howManyBlanksFound++;
                }
            } else {
                for (let i = 0; i < textField.length; i++) {
                    var textFieldNode = document.createTextNode(textField[i]);

                    taskContentReady.append(((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answer`+ self.taskName + `">&emsp;&emsp;&emsp;&emsp;</div>`));
                    taskContentReady.append(textFieldNode);

                    howManyBlanksFound++;
                }
            }

            var taskAnswerHolderReady = $(`<div class="pb-2 pt-1 mb-0 text-center mb-0 border-bottom border-gray taskAnswerHolder">`);
            for (let i = 0; i < words.length; i++) {
                var wordNode = document.createTextNode(words[i]);

                var anserDivNode = $(`<div class="answer`+ self.taskName + `">`)
                anserDivNode.append(wordNode);
                taskAnswerHolderReady.append(anserDivNode);
            }
            
            singleContentAnswerContainer.append(taskContentReady)
            singleContentAnswerContainer.append(taskAnswerHolderReady)
            $("#GameDiv").append(singleContentAnswerContainer);
        }

        var BlankContainer = (blank_, wordFillContainer_) => {
            var that = {};

            that.name = "BlankContainer";
            that.blankDiv;
            that.selected;
            that.used;
            that.text;

            that.init = (blank) => {

                that.blankDiv = $(blank);
                that.selected = false;
                that.used = false;
                that.text = $(blank).text();
                that.mainWordFillContainer = wordFillContainer_;

                that.blankDiv.on('click', (e) => {
                    if (!that.selected)
                    
                        that.mainWordFillContainer.blankWasSelected(that);
                    else {
                        that.mainWordFillContainer.elementWasUnselected();
                        that.unselect();
                    }
                })
            }

            that.unselect = () => {
                that.selected = false;
                that.blankDiv.removeClass("answer" + self.taskName + "selected");
            }

            that.select = () => {
                that.selected = true;
                that.blankDiv.addClass("answer" + self.taskName + "selected");
            }

            that.setBlankAnswerTo = (answer) => {

                that.mainWordFillContainer.checkIfAnyAnswerWasUsedForThisBlankAndChangeToNotUsedIfOtherBlanksDontUseItToo(that);
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
            that.selected;
            that.used;
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
                    } else {
                        that.mainWordFillContainer.elementWasUnselected();
                        that.unselect();
                    }
                })
            }

            that.unselect = () => {
                that.selected = false;
                that.answerDiv.removeClass("answer" + self.taskName + "selected");
            }

            that.select = () => {
                that.selected = true;
                that.answerDiv.addClass("answer" + self.taskName + "selected");
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

            that.init = (containerDiv_) => {
                that.blankElements = $($(containerDiv_).find(".taskContent")[0]).find(".answer" + self.taskName + "");
                that.answerElements = $($(containerDiv_).find(".taskAnswerHolder")[0]).find(".answer" + self.taskName + "");

                for ( let i = 0; i < that.answerElements.length; i++) {
                    var answer = that.answerElements[i];
                    that.answerContainers.push(AnswerContainer(answer, that));
                }

                for ( let i = 0; i < that.blankElements.length; i++) {
                    var blank = that.blankElements[i];
                    that.blankContainers.push(BlankContainer(blank, that));
                }

                var onclickFunction = (e) => {

                    if (self.isTaskDone) {
                        $(document).off('click', onclickFunction);
                        return;
                    }

                    var clickedElement = $(e.target);
                    var wasClickOnAnswerElement = false;

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
                        that.elementWasUnselected();
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
                    that.selectedElement = undefined;
                } else {
                    that.selectedElement = answer;
                    that.unselectAllAnswers();
                    answer.select();
                }
            }

            that.blankWasSelected = (blank) => {
                
                if ( that.selectedElement != undefined && that.selectedElement.name === "AnswerContainer") {
                    blank.setBlankAnswerTo(that.selectedElement);
                    that.selectedElement = undefined;
                } else {
                    that.selectedElement = blank;
                    that.unselectAllBlanks();
                    blank.select();
                }
            }

            that.elementWasUnselected = () => {
                that.selectedElement = undefined;
            }

            that.checkIfAnyAnswerWasUsedForThisBlankAndChangeToNotUsedIfOtherBlanksDontUseItToo = (blank) => {
                if (blank.text === "" || blank.text === undefined)
                    return;

                for (let i = 0; i < that.answerContainers.length; i++) {
                    var answer = that.answerContainers[i];

                    if ( answer.text === blank.text) {

                        var wasUsedByOtherBlanks = false;
                        for (let i = 0; i < that.blankContainers.length; i++) {
                            var currentBlank = that.blankContainers[i];

                            if ( blank === currentBlank) 
                                continue;
                            
                            if ( answer.text === currentBlank.text) {
                                wasUsedByOtherBlanks = true;
                                break;
                            }
                        }

                        if (!wasUsedByOtherBlanks)
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