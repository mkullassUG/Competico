const TaskCreatorVariant = (data_, debug = false, $jq, myWindow) =>{

    /* environment preparation */
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;

    /*  Variables */
    var self = data_;
    self.taskID;
    self.taskContent = {};
    self.taskContent.tags = [];
    self.taskContent.difficulty = 100.0;
    self.taskContent.instruction = "ToDo instruction";

    /*  Logic functions */
    var taskCreatorInit = () => { }

    self.checkIfTaskReady = () => { }

    self.prepareTaskJsonFile = () => {
        return {};
    }

    self.setupDemoFromCurrent = () => { }

    self.sendTaskVariant = (ajaxCallback, onSuccess, preparedTask) => {
        ajaxCallback(
            preparedTask,
            (data) => {
                onSuccess();
            }
        );
    }

    self.sendTaskVariantToTasksets = (ajaxCallback, onSuccess, tasksets, isJson) => {

        var preparedTask = self.prepareTaskJsonFile();
        
        ajaxCallback(
            preparedTask,
            tasksets,
            (data) => {
                onSuccess(data);
            },
            isJson
        );
    }

    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, taskID, preparedTask) => {
        ajaxCallback(
            preparedTask,
            taskID,
            (data) => {
                onSuccess(data);
            }
        );
    }

    self.sendEditedTaskVariantToTaskset = (ajaxCallback, onSuccess, taskIDs) => {

        var preparedTask = self.prepareTaskJsonFile();
        
        var promiseArray = [];
        for (let i = 0; i < taskIDs.length; i++){
            
            var taskID = taskIDs[i];
            promiseArray.push(
                ajaxCallback(
                    preparedTask,
                    taskID,
                    onSuccess
                )
            );
        }
        Promise.all(promiseArray)
        .then((results_) => {
        });
    }

    self.setupDemoFromCurrent = () => { }

    self.loadTaskFrom = (taskObject) => {
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

    /*  Initialization */
    taskCreatorInit();
    return self;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskCreatorVariant};