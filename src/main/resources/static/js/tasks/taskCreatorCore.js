const TaskCreatorCore = (data) => {
    var self = {};
    self.GameCore = TaskGameCore();

    var init = (initData) => {

    }

    self.getVariant = (variantString) => {
        var variantObject;

        //sprawdzanie
        switch (variantString) {
            case "WordFill":
                variantObject = WordFill_Creator();
                variantObject.loadTaskFrom({
                    "taskName" : "WordFill",
                    "taskContent" : {
                        "instruction" : "Complete the text with the missing words:",
                        "tags" : [ ],
                        "content" : {
                            "text" : [ "I’m sorry to have to tell you that there has been some ", " in the project and we won’t be able to ", " our original ", " on July 30th for completing the ", " of the new software. Pedro’s absence for three weeks caused a bit of a ", ", and there were more delays when we realised that there was still some ", " in the databases that needed cleaning up. Still, I am confident that we can complete the project by the end of next month." ],
                            "emptySpaces" : [ {
                                "answer" : "slippage"
                            }, {
                                "answer" : "stick to"
                            }, {
                                "answer" : "deadline"
                            }, {
                                "answer" : "rollout"
                            }, {
                                "answer" : "bottleneck"
                            }, {
                                "answer" : "dirty data"
                            } ],
                            "startWithText" : true,
                            "possibleAnswers" : [ "bottleneck", "deadline", "dirty data", "migrate", "rollout", "slippage", "stick to", "within", "scope" ]
                        },
                        "difficulty" : 100.0
                    }
                });
                break;
            case "ChronologicalOrder":
                variantObject = ChronologicalOrder_Creator();
                variantObject.loadTaskFrom({
                    "taskName" : "ChronologicalOrder",
                    "taskContent" : {
                        "instruction" : "Put the phrases in order:",
                        "tags" : [ ],
                        "sentences" : [ "Try to understand the problem and define the purpose of the program.", "Once you have analysed the problem, define the successive logical steps of the program.", "Write the instructions in a high-level language of your choice.", "Once the code is written, test it to detect bugs or errors.", "Debug and fix errors in your code.", "Finally, review the program’s documentation." ],
                        "difficulty" : 100.0
                    }
                });
                break;
            case "WordConnect":
                variantObject = WordConnect_Creator();
                variantObject.loadTaskFrom({
                    "taskName" : "WordConnect",
                    "taskContent" : {
                        "instruction" : "Match the words with their translations:",
                        "tags" : [ ],
                        "leftWords" : [ "data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier" ],
                        "rightWords" : [ "eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna" ],
                        "correctMapping" : {
                            "0" : 0,
                            "1" : 1,
                            "2" : 2,
                            "3" : 3,
                            "4" : 4,
                            "5" : 5,
                            "6" : 6,
                            "7" : 7,
                            "8" : 8,
                            "9" : 9
                        },
                        "difficulty" : 100.0
                    }
                });
                break;
            default:
                console.warn("TODO, to nie powinno się wydarzyć!")
                break;
        }

        return variantObject;
    }

    self.getVariant_GameCore = (variantString, taskToSetup) => {
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
                task.emptySpaceCount = taskToSetup.task.emptySpaces.length;

                variantObject = self.GameCore.getVariant(variantString, task);
                
                break;
            case "WordConnect":
            case "ChronologicalOrder":
                var task;
                task = taskToSetup.taskContent;
                variantObject = self.GameCore.getVariant(variantString, task);
                break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                variantObject = {};
                //ListWordFill answers
                variantObject.getAnswers = () => { 
                    console.log("hello ListWordFill");
                    return {answers: [["test"]]} 
                }
                break;
        }

        return variantObject;
    }

    init(data);
    return self;
}