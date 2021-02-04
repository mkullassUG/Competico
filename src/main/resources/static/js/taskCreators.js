const TaskCreator = (data_) =>{

    /*  Variables */
    var self = data_;
    self.taskContent = {};
    self.taskContent.tags = [];
    self.taskContent.difficulty = 100.0;
    self.taskContent.instruction = "ToDo instruction";
    self.taskContent.id = "";

    /*  Logic functions */
    var taskCreatorInit = () => {
        /*TODO
            zmieńdiva na diva konkretnego wariantu
        
        */
        
        /*NIE POWINO SIĘ TWORZYC ID PO STRONIE KLIENTA!*/
        self.taskContent.id = uuidv4();
    }
    /*NIE POWINO SIĘ TWORZYC ID PO STRONIE KLIENTA!*/
    var uuidv4 = () => {
        return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
          (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
        );
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
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }
    /*needed for task init*/
    self.hideAllTaskDivsExceptGiven = (taskName) => {
        var taskDivName = taskName + "Div";

        var taskDivs = $("#taskHolder").children();
        for (let i = 0; i < taskDivs.length; i++) {
            var taskDiv = $(taskDivs[i]);
            taskDiv.hide();
        }

        $("#"+taskDivName).show();
    }
    /*  Initialization */
    taskCreatorInit();
    return self;
}

/*TODO
będę musiał przerabiać textarea na content editable żeby miec możliwosć wstawiania html dla bardziej intuicyjnego edytowania treści taska
*/
const WordFillCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "WordFill";
    self.taskContent.content = {};
    self.taskContent.content.text = [];
    self.taskContent.content.emptySpaces = [];
    self.taskContent.content.startWithText;
    self.taskContent.content.possibleAnswers = [];

    /*  Logic functions */
    var wordFillCreatorInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);
    }

    var checkIfTaskReadySuper = self.checkIfTaskReady();
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile();
    self.prepareTaskJsonFile = () => {
        prepareTaskJsonFileSuper();
        /*TODO:
        spakuj gotowe zadanie do pliku
        */
    }

    var setupDemoFromCurrentSuper = self.setupDemoFromCurrent();
    self.setupDemoFromCurrent = () => {
        setupDemoFromCurrentSuper();
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    /*  Initialization */
    wordFillCreatorInit();
    return self;
}
/*TODO*/
const ChronologicalOrderCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "ChronologicalOrder";
    self.taskContent.sentences = [];

    /*  Logic functions */
    var chronologicalOrderCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);
        
    }

    var checkIfTaskReadySuper = self.checkIfTaskReady();
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile();
    self.prepareTaskJsonFile = () => {
        prepareTaskJsonFileSuper();
        /*TODO:
        spakuj gotowe zadanie do pliku
        */
    }

    var setupDemoFromCurrentSuper = self.setupDemoFromCurrent();
    self.setupDemoFromCurrent = () => {
        setupDemoFromCurrentSuper();
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    /*  Initialization */
    chronologicalOrderCreatorInit();
    return self;
}

/*TODO*/
const WordConnectCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "WordConnect";
    self.taskContent.leftWords = [];
    self.taskContent.rightWords = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var wordConnectCreatorInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);
    }

    var checkIfTaskReadySuper = self.checkIfTaskReady();
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile();
    self.prepareTaskJsonFile = () => {
        prepareTaskJsonFileSuper();
        /*TODO:
        spakuj gotowe zadanie do pliku
        */
    }

    var setupDemoFromCurrentSuper = self.setupDemoFromCurrent();
    self.setupDemoFromCurrent = () => {
        setupDemoFromCurrentSuper();
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    /*  Initialization */
    wordConnectCreatorInit();
    return self;
}