app.controller("incomingOperationsCtrl", ['$scope', '$rootScope', '$timeout', 'TaskOperationService', function ($scope, $rootScope, $timeout, TaskOperationService) {

    $scope.fetchThisDay = function () {
        $scope.viewType = 'حركات المهام الواردة يومياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الواردة لهذا اليوم، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findIncomingOperationsForMe("Day").then(function (data) {
            $scope.incomingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الواردة لهذا اليوم بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisWeek = function () {
        $scope.viewType = 'حركات المهام الواردة اسبوعياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الواردة لهذا الأسبوع، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findIncomingOperationsForMe("Week").then(function (data) {
            $scope.incomingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الواردة لهذا الأسبوع بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisMonth = function () {
        $scope.viewType = 'حركات المهام الواردة شهرياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الواردة لهذا الشهر، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findIncomingOperationsForMe("Month").then(function (data) {
            $scope.incomingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الواردة لهذا الشهر بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisYear = function () {
        $scope.viewType = 'حركات المهام الواردة سنوياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الواردة لهذا العام، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findIncomingOperationsForMe("Year").then(function (data) {
            $scope.incomingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الواردة لهذا العام بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.refresh = function () {
        switch ($scope.viewType) {
            case 'حركات المهام الواردة يومياً':
                $scope.fetchThisDay();
                break;
            case 'حركات المهام الواردة اسبوعياً':
                $scope.fetchThisWeek();
                break;
            case 'حركات المهام الواردة شهرياً':
                $scope.fetchThisMonth();
                break;
            case 'حركات المهام الواردة سنوياً':
                $scope.fetchThisYear();
                break;
        }
    };

    $timeout(function () {
        $scope.fetchThisDay();
    }, 2000);

}]);
