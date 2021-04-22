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
                // variantObject.loadTaskFrom({
                //     "taskName" : "WordFill",
                //     "taskContent" : {
                //         "instruction" : "Complete the text with the missing words:",
                //         "tags" : [ ],
                //         "content" : {
                //             "text" : [ "I’m sorry to have to tell you that there has been some ", " in the project and we won’t be able to ", " our original ", " on July 30th for completing the ", " of the new software. Pedro’s absence for three weeks caused a bit of a ", ", and there were more delays when we realised that there was still some ", " in the databases that needed cleaning up. Still, I am confident that we can complete the project by the end of next month." ],
                //             "emptySpaces" : [ {
                //                 "answer" : "slippage"
                //             }, {
                //                 "answer" : "stick to"
                //             }, {
                //                 "answer" : "deadline"
                //             }, {
                //                 "answer" : "rollout"
                //             }, {
                //                 "answer" : "bottleneck"
                //             }, {
                //                 "answer" : "dirty data"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "bottleneck", "deadline", "dirty data", "migrate", "rollout", "slippage", "stick to", "within", "scope" ]
                //         },
                //         "difficulty" : 100.0
                //     }
                // });
                break;
            case "ChronologicalOrder":
                variantObject = ChronologicalOrder_Creator();
                // variantObject.loadTaskFrom({
                //     "taskName" : "ChronologicalOrder",
                //     "taskContent" : {
                //         "instruction" : "Put the phrases in order:",
                //         "tags" : [ ],
                //         "sentences" : [ "Try to understand the problem and define the purpose of the program.", "Once you have analysed the problem, define the successive logical steps of the program.", "Write the instructions in a high-level language of your choice.", "Once the code is written, test it to detect bugs or errors.", "Debug and fix errors in your code.", "Finally, review the program’s documentation." ],
                //         "difficulty" : 100.0
                //     }
                // });
                break;
            case "WordConnect":
                variantObject = WordConnect_Creator();
                // variantObject.loadTaskFrom({
                //     "taskName" : "WordConnect",
                //     "taskContent" : {
                //         "instruction" : "Match the words with their translations:",
                //         "tags" : [ ],
                //         "leftWords" : [ "data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier" ],
                //         "rightWords" : [ "eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna" ],
                //         "correctMapping" : {
                //             "0" : 0,
                //             "1" : 1,
                //             "2" : 2,
                //             "3" : 3,
                //             "4" : 4,
                //             "5" : 5,
                //             "6" : 6,
                //             "7" : 7,
                //             "8" : 8,
                //             "9" : 9
                //         },
                //         "difficulty" : 100.0
                //     }
                // });
                break;
            case "ListWordFill":
                variantObject = ListWordFill_Creator();
                // variantObject.loadTaskFrom({
                //     "taskName" : "ListWordFill",
                //     "taskContent" : {
                //         "id" : "0be75481-5a7d-4703-979f-70afcdae5846",
                //         "instruction" : "Complete the sentences with the best word:",
                //         "tags" : [ ],
                //         "rows" : [ {
                //             "id" : "58f293d5-cc76-429d-a5de-2ae27924cfc8",
                //             "text" : [ "I’m ", " you asked me that question." ],
                //             "emptySpaces" : [ {
                //                 "answer" : "GLAD"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "GLAD", "SORRY", "REGRET", "INTERESTED" ]
                //             }, {
                //             "id" : "8c802a14-ba6b-4122-818f-738e2782cc9b",
                //             "text" : [ "I’m afraid I can’t say it at the ", " of my head." ],
                //             "emptySpaces" : [ {
                //                 "answer" : "TOP"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "TIP", "END", "TOP", "BACK" ]
                //             }, {
                //             "id" : "06f4e807-f467-46d3-ab6a-bec51ea2bfd9",
                //             "text" : [ "As I’ve ", " before in my presentation, …" ],
                //             "emptySpaces" : [ {
                //                 "answer" : "MENTIONED"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "SPOKEN", "MENTIONED", "SEEN", "TALKED" ]
                //             }, {
                //             "id" : "2438f4e5-9e2a-4493-9de3-b072b786fcb4",
                //             "text" : [ "Do you mind if we deal ", " it later?" ],
                //             "emptySpaces" : [ {
                //                 "answer" : "WITH"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "ON", "WITHOUT", "WITH", "FROM" ]
                //             }, {
                //             "id" : "27e6b745-68ab-4a4b-9b9f-64f8bb0fa167",
                //             "text" : [ "In fact, it goes ", " to what I was saying earlier, …" ],
                //             "emptySpaces" : [ {
                //                 "answer" : "BACK"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "BACK", "ON", "IN", "UP" ]
                //             }, {
                //             "id" : "34d0cb83-fbb3-4ae5-b712-362d0a0803c6",
                //             "text" : [ "I don’t want to go into too much ", " at this stage." ],
                //             "emptySpaces" : [ {
                //                 "answer" : "DETAIL"
                //             } ],
                //             "startWithText" : true,
                //             "possibleAnswers" : [ "DISTRUCTIONS", "DETAIL", "TIME", "DISCUSSIONS" ]
                //             } ],
                //         "difficulty" : 100.0
                //     }
                //     });
                break;
            case "ListChoiceWordFill":
                variantObject = ListChoiceWordFill_Creator();
                // variantObject.loadTaskFrom({
                //     "taskName" : "ListChoiceWordFill",
                //     "taskContent" : {
                //         "id" : "a40c7426-5662-42ec-a48d-7362d5809208",
                //         "instruction" : "Choose the best word to complete the sentences:",
                //         "tags" : [ ],
                //         "rows" : [ {
                //             "id" : "5cd63097-ffbe-4817-827b-749f113c5d65",
                //             "text" : [ "Would you mind waiting ", " Ms Bright gets back?" ],
                //             "wordChoices" : [ {
                //                 "id" : "7c88ee56-ef0a-4aa7-9c12-358c07cec370",
                //                 "correctAnswer" : "until",
                //                 "incorrectAnswers" : [ "by" ]
                //             } ],
                //             "startWithText" : true
                //             }, {
                //                 "id" : "455b4c97-cba9-4f3c-91ef-f97bebaa2ba7",
                //                 "text" : [ "By the way, could you remind everyone that our next meeting will be ", " Tuesday at 11:10?" ],
                //                 "wordChoices" : [ {
                //                 "id" : "2d9e419d-5d4a-4049-8019-891b027b0fe0",
                //                 "correctAnswer" : "on",
                //                 "incorrectAnswers" : [ "in" ]
                //                 } ],
                //                 "startWithText" : true
                //             }, {
                //                 "id" : "5ee886c1-67ef-4943-ae42-408edd0c167e",
                //                 "text" : [ "I need this report ", " 6:30 tomorrow at the latest." ],
                //                 "wordChoices" : [ {
                //                 "id" : "2cf31bc9-f3a2-4462-a063-11d33764df14",
                //                 "correctAnswer" : "by",
                //                 "incorrectAnswers" : [ "in" ]
                //                 } ],
                //                 "startWithText" : true
                //             }, {
                //                 "id" : "18560f9c-84d0-4311-bfec-6537dec42626",
                //                 "text" : [ "What did you do ", " the weekend?" ],
                //                 "wordChoices" : [ {
                //                 "id" : "c8d00301-1704-4b2e-9be2-8f89d215902d",
                //                 "correctAnswer" : "at",
                //                 "incorrectAnswers" : [ "on" ]
                //                 } ],
                //                 "startWithText" : true
                //             }, {
                //                 "id" : "b717980d-59ac-498e-b72b-91d8c416f188",
                //                 "text" : [ "Harry has ", " decided which university he wants to go." ],
                //                 "wordChoices" : [ {
                //                 "id" : "08aaea02-a95f-4a31-a976-b8b72bf468bf",
                //                 "correctAnswer" : "already",
                //                 "incorrectAnswers" : [ "before" ]
                //                 } ],
                //                 "startWithText" : true
                //             }, {
                //                 "id" : "cd821236-0e5f-42d9-b207-a1f13f00dec4",
                //                 "text" : [ "Luckily, we landed exactly ", ", so we were able to catch our connecting flight." ],
                //                 "wordChoices" : [ {
                //                 "id" : "8882f1c1-f084-4106-a4ac-7aba25bbe5ee",
                //                 "correctAnswer" : "on time",
                //                 "incorrectAnswers" : [ "in time" ]
                //                 } ],
                //                 "startWithText" : true
                //             }, {
                //                 "id" : "dd252be4-efa0-425d-9ffd-7754be488db3",
                //                 "text" : [ " I got used to the new interface, it didn’t feel awkward anymore." ],
                //                 "wordChoices" : [ {
                //                 "id" : "fb6dc4b9-f1ba-4d6d-bea7-e6ae884410d0",
                //                 "correctAnswer" : "Once",
                //                 "incorrectAnswers" : [ "One day" ]
                //                 } ],
                //                 "startWithText" : false
                //             }, {
                //                 "id" : "4865d732-0ae6-47f6-a572-ff904a22e7bc",
                //                 "text" : [ "He’s been trying to solve this problem ", " three hours." ],
                //                 "wordChoices" : [ {
                //                 "id" : "b26d8da3-064c-4eab-8ecf-21966011ba86",
                //                 "correctAnswer" : "for",
                //                 "incorrectAnswers" : [ "since" ]
                //                 } ],
                //                 "startWithText" : false
                //             } ],
                //         "difficulty" : 100.0
                //     }
                //     });
                //TODO
                // variantObject.loadTaskFrom({});
                break;
            case "ListSentenceForming":
                variantObject = ListSentenceForming_Creator();
                // variantObject.loadTaskFrom({
                //     "taskName" : "ListSentenceForming",
                //     "taskContent" : {
                //       "id" : "5b4dd59a-795a-4c05-9eb2-abf1ea39260a",
                //       "instruction" : "Put the words in order to make meaningful sentences.",
                //       "tags" : [ ],
                //       "difficulty" : 150.0,
                //       "rows" : [ {
                //         "id" : "28ccdfae-479a-48c6-9e1d-8b7ec41bb6ba",
                //         "words" : [ "In", "1983", "Apple", "introduced", "the", "first", "commercial", "personal", "computer" ]
                //       }, {
                //         "id" : "a0fbd588-2d3f-4a2e-b49c-768e7093ae85",
                //         "words" : [ "It", "was", "his", "third", "week", "in", "the", "office", "and", "he", "was", "still", "learning", "touch-typing" ]
                //       }, {
                //         "id" : "4ee814ee-4ef8-41c8-8dde-7e1612af3df3",
                //         "words" : [ "It", "was", "becoming", "more", "and", "more", "common", "to", "outsource", "the", "helpline", "service", "to", "India" ]
                //       }, {
                //         "id" : "59219b73-81b6-4ef3-a545-d8824915e16d",
                //         "words" : [ "When", "I", "got", "to", "the", "office,", "I", "realized", "I", "had", "left", "my", "documents", "at", "home" ]
                //       }, {
                //         "id" : "6004bcac-7d8f-4272-ad4f-d5ef8e76800b",
                //         "words" : [ "As", "an", "employee", "of", "this", "company,", "I", "designed", "data", "marts." ]
                //       }, {
                //         "id" : "28f7bf5e-25b6-4e30-91c5-6ccc222a5d45",
                //         "words" : [ "Jim", "had", "been", "working", "there", "since", "2012", "until", "last", "Friday", "when", "the", "company", "went", "bankrupt" ]
                //       } ]
                //     }
                //   });
                // TODO
                // variantObject.loadTaskFrom({});
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
                task.emptySpaceCount = taskToSetup.taskContent.content.emptySpaces.length;

                variantObject = self.GameCore.getVariant(variantString, task);
                
                break;
            case "WordConnect":
            case "ChronologicalOrder":
                var task;
                task = taskToSetup.taskContent;
                variantObject = self.GameCore.getVariant(variantString, task);
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

                variantObject = self.GameCore.getVariant(variantString, task);
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

                console.log(text)
                console.log(wordChoices)
                console.log(startWithText)
                task.text = text;
                task.wordChoices = wordChoices;
                task.startWithText = startWithText;

                console.log(task)
                variantObject = self.GameCore.getVariant(variantString, task);
                break;
            case "ListSentenceForming":
                //TODO
                var task;
                task = taskToSetup.taskContent;
                task.words = task.rows.map(r => r.words);
                console.log(task);

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