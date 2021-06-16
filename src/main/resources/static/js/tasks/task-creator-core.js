const TaskCreatorCore = (debug = false, $jq, myWindow, deps = {}, cbTest) => {

    /* singleton */
    if (TaskCreatorCore.singleton)
        return TaskCreatorCore.singleton;
    var self = {};
    if (!TaskCreatorCore.singleton)
        TaskCreatorCore.singleton = self;
        
    /* environment preparation */
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;
    if ( deps.TaskGameCore && typeof TaskGameCore == "undefined")
        TaskGameCore = deps.TaskGameCore;

    /*       logic variables          */
    self.GameCore;
    self.variantObject;

    /*       logic functions          */
    var TaskCreatorCoreInit = (initDeps) => {
        
        if (debug)
            console.log("TaskCreatorCore init");

        self.GameCore = TaskGameCore();

        if ( cbTest )
            cbTest("success");
    }

    self.getVariant = (variantString) => {

        //jeśli niepodano nazwy to wysyłam obecny variant
        if ( !variantString)
            if ( self.variantObject )
                return self.variantObject
            else
                return false;

        self.variantObject = {};

        //sprawdzanie
        switch (variantString) {
            case "WordFill":
                self.variantObject = WordFill_Creator();
                break;
            case "ChronologicalOrder":
                self.variantObject = ChronologicalOrder_Creator();
                break;
            case "WordConnect":
                self.variantObject = WordConnect_Creator();
                break;
            case "ListWordFill":
                self.variantObject = ListWordFill_Creator();
                break;
            case "ListChoiceWordFill":
                self.variantObject = ListChoiceWordFill_Creator();
                break;
            case "ListSentenceForming":
                self.variantObject = ListSentenceForming_Creator();
                break;
            case "OptionSelect":
                self.variantObject = OptionSelect_Creator();
                break;
            
            default:
                console.warn("TODO, to nie powinno się wydarzyć!")
                break;
        }

        return self.variantObject;
    }

    self.getVariant_GameCore = (variantString, taskToSetup) => {

        //jeśli niepodano nazwy i obiektu taska to wysyłam obecny variant
        if ( !variantString || !taskToSetup)
            if ( self.GameCore.getVariant() )
                return self.GameCore.getVariant();
            else {
                console.warn("To nie powinno się wydarzyć!")
                return false;
            }

        variantString = JSON.parse(JSON.stringify(variantString));
        taskToSetup = JSON.parse(JSON.stringify(taskToSetup));

        var variantObject;

        //w gameLogic jest setupNewTask, czy chce zrobić grugie takie tutaj?
        //start setupNewTask gameLogic
        //czy otrzymany task jest pusty
        if (variantString === null) {
            if (self.debug)
            console.warn("task was empty");
            return;
        }

        if (!variantString)
            console.warn("Could not read task name!");
        
        //wybieranie odpowiedniej logiki dla konkretnego template'a
        switch (variantString) {
            case "WordFill":
                var task;
                task = taskToSetup.taskContent.content;
                task.emptySpaceCount = taskToSetup.taskContent.content.emptySpaces.length;
                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            case "WordConnect":
            case "ChronologicalOrder":
                var task;
                task = taskToSetup.taskContent;
                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            case "ListWordFill":    
                var task;
                task = taskToSetup.taskContent;
                var possibleAnswers = task.rows.map((row) => row.possibleAnswers);
                var text = task.rows.map((row) => row.text);
                var emptySpaceCount = task.rows.map((row) => row.emptySpaces.length);
                var startWithText = task.rows.map((row) => row.startWithText);

                task.possibleAnswers = possibleAnswers;
                task.text = text;
                task.emptySpaceCount = emptySpaceCount;
                task.startWithText = startWithText;
                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            case "ListChoiceWordFill":
                var task;
                task = taskToSetup.taskContent;

                var text = task.rows.map((row) => row.text);
                var wordChoices = task.rows.map((row) => {
                    return row.wordChoices.map((choice)=>[...choice.incorrectAnswers, choice.correctAnswer])
                });
                //var wordChoices = task.rows.map((row) => [...row.wordChoices.incorrectAnswers, row.wordChoices.correctAnswer]);
                var startWithText = task.rows.map((row) => row.startWithText);

                task.text = text;
                task.wordChoices = wordChoices;
                task.startWithText = startWithText;

                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            case "ListSentenceForming":
                var task;
                task = taskToSetup.taskContent;
                task.words = task.rows.map(r => r.words);
                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            case "OptionSelect":
                var task;
                task = taskToSetup.taskContent;
                task.answers = [...task.content.incorrectAnswers, ...task.content.correctAnswers];
                task.content = task.content.content; // -.-
                delete task.content.incorrectAnswers;
                delete task.content.correctAnswers;
                delete task.content.content;
                var instruction = taskToSetup.taskContent.instruction;

                variantObject = self.GameCore.getVariant(variantString, task, instruction);
                break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                variantObject = {};
                //ListWordFill answers
                variantObject.getAnswers = () => { 
                    return {answers: [["test"]]} 
                }
                break;
        }

        return variantObject;
    }

    TaskCreatorCoreInit(deps);
    return self;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskCreatorCore};