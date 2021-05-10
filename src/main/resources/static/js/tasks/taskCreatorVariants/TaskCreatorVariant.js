const TaskCreatorVariant = (data_, debug = false, $jq, myWindow) =>{

    /* environment preparation */
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;

    /*  Variables */
    var self = data_;
    self.taskID; //do edycji
    self.taskContent = {};
    self.taskContent.tags = [];
    self.taskContent.difficulty = 100.0;
    self.taskContent.instruction = "ToDo instruction";

    /*  Logic functions */
    var taskCreatorInit = () => {
        /*TODO
            zmieńdiva na diva konkretnego wariantu
        
        */
        
    }

    self.checkIfTaskReady = () => {
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu:

        1. żadne pole nie jest puste
        2. od ostatniego zapisu zaszły zmiany
        2.1 task nie jest identyczny z innym taskiem z listy istniejącym???
        */
    }

    self.prepareTaskJsonFile = () => {
        
        /*TODO:
        spakuj gotowe zadanie do pliku*/
        return {};
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }
    //wysłanie nowego
    self.sendTaskVariant = (ajaxCallback, onSuccess, preparedTask) => {
        ajaxCallback(
            preparedTask,
            (data) => {
                onSuccess(); // self.setupImportedTasksTable();
            }
        );
    }
    //TODO edycja starego 
    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, taskID, preparedTask) => {
        ajaxCallback(
            preparedTask,
            taskID,
            (data) => {
                onSuccess(); // self.setupImportedTasksTable();
            }
        );
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    self.loadTaskFrom = (taskObject) => {
        /*TODO:
        wczytanie zadania na ekran z obiektu lub pliku json*/
        self.taskContent = taskObject.taskContent;
    }

    /*needed for task init*/
    self.hideAllTaskDivsExceptGiven = (taskName) => {
        var taskDivName = taskName + "Div";

        var taskDivs = $("#taskEditHolder").children();
        for (let i = 0; i < taskDivs.length; i++) {
            var taskDiv = $(taskDivs[i]);
            taskDiv.hide();
        }

        $("#"+taskDivName).show();
    }

    /*Could do:*/
    /*
    self.prepareTaskCreatorButtons = (taskName) => {
        //wszystkie przyciski zaczynające się na [btn]+[taskName]+*

        //klonować i podmienić...

        //ale jakie funkcje wstawić? z tego miejsca ich nie widzę
        
        //trzeba zrobić lokalnie dla pojedynczych tasków (zeby nie robić w init wszystkiego)?
    }
    */


    /*  Initialization */
    taskCreatorInit();
    return self;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskCreatorVariant};