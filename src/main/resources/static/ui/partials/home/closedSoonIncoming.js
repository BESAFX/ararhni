app.controller("closedSoonIncomingCtrl", ['$scope', '$rootScope', '$timeout', 'TaskService', 'ModalProvider',
    function ($scope, $rootScope, $timeout, TaskService, ModalProvider) {

        $scope.selected = {};

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.tasks, function (task) {
                    if (object.id == task.id) {
                        $scope.selected = task;
                        return task.isSelected = true;
                    } else {
                        return task.isSelected = false;
                    }
                });
            }
        };

        $scope.fetchThisDay = function () {
            $scope.viewType = 'المهام الواردة المغلقة هذا اليوم';
            $rootScope.showNotify("الرئيسية", "جاري تحميل المهام المغلقة لهذا اليوم، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('isTaskOpen=');
            search.push(true);
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Day');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الرئيسية", "تم تحميل المهام المغلقة لهذا اليوم بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisWeek = function () {
            $scope.viewType = 'المهام الواردة المغلقة هذا الاسبوع';
            $rootScope.showNotify("الرئيسية", "جاري تحميل المهام المغلقة لهذا الأسبوع، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('isTaskOpen=');
            search.push(true);
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Week');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الرئيسية", "تم تحميل المهام المغلقة لهذا الأسبوع بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisMonth = function () {
            $scope.viewType = 'المهام الواردة المغلقة هذا الشهر';
            $rootScope.showNotify("الرئيسية", "جاري تحميل المهام المغلقة لهذا الشهر، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('isTaskOpen=');
            search.push(true);
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Month');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الرئيسية", "تم تحميل المهام المغلقة لهذا الشهر بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisYear = function () {
            $scope.viewType = 'المهام الواردة المغلقة هذا العام';
            $rootScope.showNotify("الرئيسية", "جاري تحميل المهام المغلقة لهذا العام، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('isTaskOpen=');
            search.push(true);
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Year');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الرئيسية", "تم تحميل المهام المغلقة لهذا العام بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.refresh = function () {
            switch ($scope.viewType){
                case 'المهام الواردة المغلقة هذا اليوم':
                    $scope.fetchThisDay();
                    break;
                case 'المهام الواردة المغلقة هذا الاسبوع':
                    $scope.fetchThisWeek();
                    break;
                case 'المهام الواردة المغلقة هذا الشهر':
                    $scope.fetchThisMonth();
                    break;
                case 'المهام الواردة المغلقة هذا العام':
                    $scope.fetchThisYear();
                    break;
            }
        };

        $scope.openTasksOperationsReportModel = function () {
            ModalProvider.openTaskOperationsReportModel($scope.tasks);
        };

        $scope.openReportTasksModel = function () {
            ModalProvider.openTasksReportModel($scope.tasks);
        };

        $timeout(function () {
            $scope.fetchThisDay();
        }, 2000);

    }]);
