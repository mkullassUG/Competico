const DualListModule = (function(deps={}){
		
    if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
        throw Error("jQuery not defined");
    else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
        deps.$ = $;

    if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
        throw Error("window not defined");
    else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
        deps.window = window;
    
    const DualListLogic = (data, debug = false, successfulCreationCallback) => {

        var self = {};
        self.createdSuccessfully = false;
        self.allOptions = [];
        self.inputOptions = [];
        self.outputOptions = [];
        self.filteredOutputOptions = [];
        self.filteredInputOptions = [];
        
        self.outsideTasks = 0;

        var outputSel, inputSel, duallist, inputMoveMulti, inputMoveOne, outputMoveMulti, outputMoveOne,
        inputTextFilter, outputTextFilter;
        
        var DualListInit = (selector, tasksets = []) => {
            
            duallist = deps.$(selector);
            outputSel = deps.$(duallist.find(".outputSel")[0]);
            inputSel = deps.$(duallist.find(".inputSel")[0]); 
            inputMoveMulti = deps.$(duallist.find(".input-move-multi")[0]);
            inputMoveOne = deps.$(duallist.find(".input-move-one")[0]);
            outputMoveMulti = deps.$(duallist.find(".output-move-multi")[0]);
            outputMoveOne = deps.$(duallist.find(".output-move-one")[0]);
            inputTextFilter = deps.$(duallist.find(".input-text-filter")[0]);
            outputTextFilter = deps.$(duallist.find(".output-text-filter")[0]);
            
            if ( duallist.length > 0 && outputSel.length > 0 && inputSel.length > 0)
                self.createdSuccessfully = true;
            
            self.refresh();
            if ( tasksets.length )
                self.insertOptions(tasksets);
            updateCounters();
            listenersSetup();

            if (successfulCreationCallback)
                successfulCreationCallback(self);
        }

        //listeners
        var listenersSetup = () => {
            inputTextFilter.on("change input click", (e)=> {
                //filter 
                self.checkAndSetupFilterFor(inputTextFilter.val(), inputSel);
                updateCounters();
            });
            
            outputTextFilter.on("change input click", (e)=> {
                //filter 
                updateCounters();
            });
            
            inputMoveMulti.on("click",(e)=> {
                moveToList(
                    getAllOptionsFrom(inputSel),
                    outputSel
                );
                updateCounters();
            });
            inputMoveOne.on("click",(e)=> {
                moveToList(
                    getSelected(inputSel),
                    outputSel
                );
                updateCounters();
            });
            outputMoveMulti.on("click",(e)=> {
                moveToList(
                    getAllOptionsFrom(outputSel),
                    inputSel
                );
                updateCounters();
            });
            outputMoveOne.on("click",(e)=> {
                moveToList(
                    getSelected(outputSel),
                    inputSel
                );
                updateCounters();
            });
        }
        
        //pobranie podaych
        var getGiven = (options) => {
            var selectedNodes = [];
            var selectedValues = [];
            options.each((e,b)=>{selectedNodes.push(b);selectedValues.push(b.value)});
            
            var ret = {values: selectedValues, nodes: selectedNodes}

            return ret;
        }
        //pobranie zaznaczonych
        var getSelected = (selectElement) =>  getGiven(selectElement.children("option:selected"));
        //pobranie z listy output
        var getAllOptionsFrom = (selectElement) => getGiven(selectElement.children());

        //filtrowanie:
        var filterOptions = (text, selectElement) => {

            //przywróć wszystkie 
            self.insertOptions(self.filteredInputOptions);
            self.inputOptions = deps.$.merge(self.inputOptions, self.filteredInputOptions);
            self.filteredInputOptions = [];

            var foundNodes = [];
            var foundValues = [];
            if (text.trim() === "" )
                return {values: foundValues, nodes: foundNodes}

            //filtruj
            var options = getAllOptionsFrom(selectElement).nodes;
            deps.$(options).each((e,b)=>{
                if ( !b.value.includes(text.trim()) ){
                    foundNodes.push(b);
                    foundValues.push(b.value);
                    self.filteredInputOptions.push(b.value);
                    self.inputOptions.splice(self.inputOptions.indexOf(b.value), 1);
                } 
            });
            
            return {values: foundValues, nodes: foundNodes};
        }

        //przenoszenie do drugiej listy:
        var moveToList = (optArray, selectNodeTo) => {

            var nodes = optArray.nodes;
            var values = optArray.values;
            deps.$(nodes).prop("selected", false);
            selectNodeTo.prepend(nodes)

            if ( selectNodeTo.hasClass("outputSel") ){
                self.outputOptions = deps.$.merge(self.outputOptions, values);
                self.inputOptions = self.inputOptions.filter(t => !values.includes(t));
            } else {
                self.inputOptions = deps.$.merge(self.inputOptions, values);
                self.outputOptions = self.outputOptions.filter(t => !values.includes(t));
            }
            
        };

        var updateCounters = () => {
            var inOpts = getAllOptionsFrom(inputSel).nodes;
            var outOpts = getAllOptionsFrom(outputSel).nodes;

            var text = "Showing all ";
            if ( self.filteredInputOptions.length )
                text = "After filter showing ";
            deps.$("#dualList > div:nth-child(1) > div:nth-child(2) > span > span").text(text + inOpts.length);
            
            text = "Showing all "
            if ( self.filteredOutputOptions.length )
                text = "After filter showing ";
            deps.$("#dualList > div:nth-child(2) > div:nth-child(2) > span > span").text(text + outOpts.length);

            
            var inLab2 = deps.$("#duallist-non-selected-label-part2");
            inLab2.text("\n" + self.inputOptions.length);
            inLab2.html(inLab2.html().replace(/\n/g,'<br/>'));

            var outLab1 = deps.$("#duallist-selected-label-part1");
            var outLab2 = deps.$("#duallist-selected-label-part2");
            if ( (self.outputOptions.length + self.outsideTasks) > 0){
                outLab1.css({"color":"inherit"});
                outLab2.css({"color":"inherit"});
            } else {
                outLab1.css({"color":"red"});
                outLab2.css({"color":"red"});
            }

            outLab2.text("\n" + (self.outputOptions.length + self.outsideTasks));
            outLab2.html(outLab2.html().replace(/\n/g,'<br/>'));
        }

        self.updateSaveToTasksetInfo = (add = 0) => {
            self.outsideTasks = add;
            updateCounters();
        }

        //pobranie opcji
        self.insertOptions = (optArray) => {
            
            optArray.forEach((opt) => {

                var textNode = deps.window.document.createTextNode(opt);
                var optNode = deps.$(`<option>`).append(textNode).attr("value", opt);

                inputSel.append(optNode);
                self.allOptions.push(opt);
                self.inputOptions.push(opt);
            });

            inputSel.children().sort(sort_opt).appendTo(inputSel);
            function sort_opt(a, b) {
                return (deps.$(b).val()) < (deps.$(a).val()) ? 1 : -1;
            }
        }

        //zaznaczenie np przefiltrowanych:re
        var selectOpts = (optArray, selectNode) => selectNode.val(optArray);

        self.move = (optArr) => {

            selectOpts(optArr,inputSel);
            selectOpts(optArr,outputSel);

            moveToList(
                getSelected(outputSel),
                inputSel
            );
            moveToList(
                getSelected(inputSel),
                outputSel
            );
            updateCounters();
        }
        //refresh duallist
        self.refresh = () => {
            self.allOptions = [];
            self.inputOptions = [];
            self.outputOptions = [];
            self.filteredOutputOptions = [];
            self.filteredInputOptions = [];
            self.outsideTasks = 0;
            inputTextFilter.val("");
            outputTextFilter.val("");
            inputSel.empty();
            outputSel.empty();
            updateCounters();
        }

        self.getOutput = () => getAllOptionsFrom(outputSel).values;
        
        self.insertIntoInputFilter = (text) => inputTextFilter.val(text);
        
        var filteredOptionsFor = (text, selectElement) => filterOptions(text, selectElement);
        
        self.checkAndSetupFilterFor = (text, selectElement) => {
            var filtered = filteredOptionsFor(text, selectElement);

            filtered.nodes.forEach(opt => deps.$(opt).detach());
        }

        self.tasksetAlreadyExists = (tasksetName) => {

            var ret = false;
            if ( self.allOptions.includes(tasksetName))
                ret = true;
            return ret;
        }

        DualListInit(data.selector, data.tasksets);
        return self;
    }
    
    return {
        DualListLogic: DualListLogic
    }
});
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {DualListModule};
