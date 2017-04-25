app.controller('taskDetailsCtrl', ['ModalProvider', 'TaskToService', 'TaskService', 'TaskCloseRequestService', 'TaskOperationService', 'TaskWarnService', 'TaskDeductionService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, TaskToService, TaskService, TaskCloseRequestService, TaskOperationService, TaskWarnService, TaskDeductionService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.refreshTask = function () {
            TaskService.findOne(task.id).then(function (data) {
                $scope.task = data;
            });
        };

        $scope.refreshTaskCloseRequests = function () {
            var search = [];
            search.push('taskId=');
            search.push($scope.task.id);
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.task.taskCloseRequests = data;
            })
        };

        $scope.refreshTaskTo = function () {
            TaskToService.findByTask($scope.task.id).then(function (data) {
                $scope.task.taskTos = data;
            })
        };

        $scope.openCreateOperationModel = function () {
            ModalProvider.openTaskOperationCreateModel($scope.task);
        };

        $scope.openProgressModel = function () {
            ModalProvider.openTaskProgressModel($scope.task);
        };

        $scope.openClosedModel = function () {
            ModalProvider.openTaskClosedModel($scope.task);
        };

        $scope.openExtensionModel = function () {
            ModalProvider.openTaskExtensionModel($scope.task);
        };

        $scope.openClearWarnsAndDeductionsModel = function () {
            ModalProvider.openClearCountersModel($scope.task);
        };

        $scope.openRequestCloseModel = function () {
            ModalProvider.openTaskRequestCloseModel($scope.task, true);
        };

        $scope.openRequestExtensionModel = function () {
            ModalProvider.openTaskRequestCloseModel($scope.task, false);
        };

        $scope.openReportTaskOperationsModel = function () {
            ModalProvider.openTaskOperationsReportModel([$scope.task]);
        };

        $scope.openCreateWarnModel = function () {
            ModalProvider.openTaskWarnCreateModel($scope.task);
        };

        $scope.openCreateDeductionModel = function () {
            ModalProvider.openTaskDeductionCreateModel($scope.task);
        };

        $scope.openCreateTaskToModel = function () {
            ModalProvider.openTaskToCreateModel($scope.task);
        };

        $scope.openRemoveTaskToModel = function () {
            ModalProvider.openTaskToRemoveModel($scope.task);
        };

        $scope.openTaskToOpenDialog = function () {
            ModalProvider.openTaskToOpenModel($scope.task);
        };

        $scope.findTaskOperations = function () {
            TaskOperationService.findByTask($scope.task).then(function (data) {
                $scope.task.taskOperations = data;
            })
        };

        $scope.findTaskWarns = function () {
            TaskWarnService.findByTask($scope.task).then(function (data) {
                $scope.task.taskWarns = data;
            });
        };

        $scope.findTaskDeductions = function () {
            TaskDeductionService.findByTask($scope.task).then(function (data) {
                $scope.task.taskDeductions = data;
            })
        };

        $scope.acceptRequest = function (taskCloseRequest) {
            if (taskCloseRequest.type) {
                ModalProvider.openTaskClosedModel(task).result.then(function (data) {
                    if (data) {
                        TaskService.acceptRequest(taskCloseRequest.id).then(function (data) {
                            $scope.refreshTaskCloseRequests();
                            $scope.findTaskOperations();
                        });
                    }
                });
            } else {
                ModalProvider.openTaskExtensionModel(task).result.then(function (data) {
                    if (data) {
                        TaskService.acceptRequest(taskCloseRequest.id).then(function (data) {
                            $scope.refreshTaskCloseRequests();
                            $scope.findTaskOperations();
                        });
                    }
                });
            }
        };

        $scope.declineRequest = function (taskCloseRequest) {
            if (taskCloseRequest.type) {
                $rootScope.showConfirmNotify("المهام", "هل تود رفض طلب الإغلاق فعلاً؟", "error", "fa-black-tie", function () {
                    TaskService.declineRequest(taskCloseRequest.id).then(function (data) {
                        $scope.refreshTaskCloseRequests();
                        $scope.findTaskOperations();
                    });
                });
            } else {
                $rootScope.showConfirmNotify("المهام", "هل تود رفض طلب التمديد فعلاً؟", "error", "fa-black-tie", function () {
                    TaskService.declineRequest(taskCloseRequest.id).then(function (data) {
                        $scope.refreshTaskCloseRequests();
                        $scope.findTaskOperations();
                    });
                });
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.deleteTaskCloseRequests = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع طلبات الإغلاق فعلاً؟", "error", "fa-trash", function () {
                TaskCloseRequestService.deleteByTaskAndType($scope.task.id, true).then(function (data) {
                    $scope.refreshTaskCloseRequests();
                });
            });
        };

        $scope.deleteTaskExtendRequests = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع طلبات التمديد فعلاً؟", "error", "fa-trash", function () {
                TaskCloseRequestService.deleteByTaskAndType($scope.task.id, false).then(function (data) {
                    $scope.refreshTaskCloseRequests();
                });
            });
        };

        $scope.clearAllWarns = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع التحذيرات فعلاً؟", "error", "fa-trash", function () {
                TaskWarnService.clearAllCounters($scope.task.id).then(function (data) {
                    $scope.findTaskWarns();
                });
            });
        };

        $scope.clearAllDeductions = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع الخصومات فعلاً؟", "error", "fa-trash", function () {
                TaskDeductionService.clearAllCounters($scope.task.id).then(function (data) {
                    $scope.findTaskDeductions();
                });
            });
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);