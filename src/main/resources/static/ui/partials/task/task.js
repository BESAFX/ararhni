app.controller("taskCtrl", ['TaskService', 'TaskOperationService', 'TaskCloseRequestService', 'PersonService', 'ReportModelService', 'ModalProvider', '$scope', '$rootScope', '$log', '$timeout', '$state',
    function (TaskService, TaskOperationService, TaskCloseRequestService, PersonService, ReportModelService, ModalProvider, $scope, $rootScope, $log, $timeout, $state) {

        $timeout(function () {

            $scope.sideOpacity = 1;

            $scope.buffer = {};

            $scope.buffer.taskType = true;

            $scope.buffer.isTaskOpen = true;

            $scope.reportModel = {};

            $scope.reportProp = {};

            $scope.reportProp.exportType = 'pdf';

            $scope.reportProp.orientation = 'Portrait';

            $scope.reportProp.title = 'تقرير مهام إدارية';

            $scope.reportProp.columns = [
                {
                    "name": "رقم المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "عنوان المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تفاصيل المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تاريخ إنشاء المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center", "viewProp": false
                },
                {
                    "name": "تاريخ تسليم المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center", "viewProp": false
                },
                {
                    "name": "جهة التكليف",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "رقم الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تاريخ الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "محتوى الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "مدخل الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                }
            ];

            $rootScope.showToast("جاري تحميل كل المهام الواردة والسارية الآن، فضلاً انتظر قليلاً");

            $scope.selected = {};

            $scope.selectedOperation = {};

            ReportModelService.findAll().then(function (data) {
                $scope.reportModels = data;
                $rootScope.showToast("تم تحميل نماذج الطباعة بنجاح");
            });

            PersonService.findPersonUnderMe().then(function (data) {
                $scope.persons = data;
                $scope.buffer.person = data[0];
                $scope.filter();
                $rootScope.showToast("تم تحميل كل المهام الواردة والسارية بنجاح");
            });

        }, 2000);

        $scope.setReportProp = function () {
            $scope.reportProp = JSON.parse($scope.buffer.reportModel.template);
        };

        $scope.filter = function () {
            $rootScope.showToast("جاري تصفية المهام، فضلاً انتظر قليلاً");
            var search = [];

            if ($scope.buffer.title) {
                search.push('title=');
                search.push($scope.buffer.title);
                search.push('&');
            }
            if ($scope.buffer.codeFrom) {
                search.push('codeFrom=');
                search.push($scope.buffer.codeFrom);
                search.push('&');
            }
            if ($scope.buffer.codeTo) {
                search.push('codeTo=');
                search.push($scope.buffer.codeTo);
                search.push('&');
            }
            if ($scope.buffer.startDateTo) {
                search.push('startDateTo=');
                search.push($scope.buffer.startDateTo.getTime());
                search.push('&');
            }
            if ($scope.buffer.startDateFrom) {
                search.push('startDateFrom=');
                search.push($scope.buffer.startDateFrom.getTime());
                search.push('&');
            }
            if ($scope.buffer.endDateTo) {
                search.push('endDateTo=');
                search.push($scope.buffer.endDateTo.getTime());
                search.push('&');
            }
            if ($scope.buffer.endDateFrom) {
                search.push('endDateFrom=');
                search.push($scope.buffer.endDateFrom.getTime());
                search.push('&');
            }

            search.push('isTaskOpen=');
            search.push($scope.buffer.isTaskOpen);
            search.push('&');

            search.push('taskType=');
            search.push($scope.buffer.taskType);
            search.push('&');

            search.push('person=');
            search.push($scope.buffer.person.id);
            search.push('&');

            search.push('timeType=');
            search.push('All');
            search.push('&');

            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                angular.forEach(data, function (task) {
                    return task.tos = task.taskTos.map(function (a) {
                        return a.person.name
                    });
                });
                $scope.setSelected(data[0]);
                $rootScope.showToast("تم التحميل بنجاح، يمكنك متابعة عملك الآن");
            });
        };

        $scope.printFilteredTasks = function () {
            $rootScope.showToast("جاري إعداد التقرير، فضلاً انتظر قليلاً");
            var search = [];

            if ($scope.buffer.codeFrom) {
                search.push('codeFrom=');
                search.push($scope.buffer.codeFrom);
                search.push('&');
            }
            if ($scope.buffer.codeTo) {
                search.push('codeTo=');
                search.push($scope.buffer.codeTo);
                search.push('&');
            }
            if ($scope.buffer.startDateTo) {
                search.push('startDateTo=');
                search.push($scope.buffer.startDateTo.getTime());
                search.push('&');
            }
            if ($scope.buffer.startDateFrom) {
                search.push('startDateFrom=');
                search.push($scope.buffer.startDateFrom.getTime());
                search.push('&');
            }
            if ($scope.buffer.endDateTo) {
                search.push('endDateTo=');
                search.push($scope.buffer.endDateTo.getTime());
                search.push('&');
            }
            if ($scope.buffer.endDateFrom) {
                search.push('endDateFrom=');
                search.push($scope.buffer.endDateFrom.getTime());
                search.push('&');
            }

            search.push('isTaskOpen=');
            search.push($scope.buffer.isTaskOpen);
            search.push('&');

            search.push('timeType=');
            search.push('All');
            search.push('&');

            search.push('taskType=');
            search.push($scope.buffer.taskType);
            search.push('&');

            search.push('person=');
            search.push($scope.buffer.person.id);
            search.push('&');

            console.info(search.join(""));

            TaskService.reportFilteredTasks(search.join(""), $scope.reportProp);
        };

        $scope.onclose = function () {
            $scope.showFilter = false;
            $scope.showCommentOperation = false
        };

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

        $scope.setSelectedOperation = function (object) {
            if (object) {
                angular.forEach($scope.selected.taskOperations, function (taskOperation) {
                    if (object.id == taskOperation.id) {
                        $scope.selectedOperation = taskOperation;
                        return taskOperation.isSelected = true;
                    } else {
                        return taskOperation.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.taskType = true;
            $scope.buffer.isTaskOpen = true;
            $scope.buffer.person = $scope.persons[0];
        };

        $scope.delete = function (task) {
            if (task) {
                TaskService.remove(task.id);
                return;
            }
            TaskService.remove($scope.selected);
        };

        $scope.openCreateModel = function () {
            ModalProvider.openTaskCreateModel();
        };

        $scope.openUpdateModel = function (task) {
            if (task) {
                ModalProvider.openTaskUpdateModel(task);
                return;
            }
            ModalProvider.openTaskUpdateModel($scope.selected);
        };

        $scope.openOperationModel = function (task) {
            if (task) {
                ModalProvider.openTaskOperationModel(task);
                return;
            }
            ModalProvider.openTaskOperationModel($scope.selected);
        };

        $scope.openRequestCloseModel = function (task) {
            if (task) {
                ModalProvider.openTaskRequestCloseModel(task);
                return;
            }
            ModalProvider.openTaskRequestCloseModel($scope.selected);
        };

        $scope.showSlideFilter = function () {
            $scope.showSlide = true;
            $scope.sideSize = '50%';
            $scope.showFilter = true;
        };

        $scope.showSlideOperation = function () {
            $scope.showSlide = true;
            $scope.sideSize = '100%';
            $scope.showOperation = true;
        };

        $scope.findTaskOperations = function () {
            TaskOperationService.findByTask($scope.selected).then(function (data) {
                $scope.selected.taskOperations = data;
            })
        };

        $scope.findTaskCloseRequests = function () {
            $rootScope.showToast("جاري تحميل طلبات الإغلاق، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskId=');
            search.push($scope.selected.id);
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.selected.taskCloseRequests = data;
                $rootScope.showToast("تم تحميل طلبات الإغلاق بنجاح");
            })
        };

        $scope.openCreateOperationModel = function (task) {
            if (task) {
                ModalProvider.openTaskOperationCreateModel(task);
                return;
            }
            ModalProvider.openTaskOperationCreateModel($scope.selected);
        }

        $scope.rowMenu = [];

        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_CREATE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-plus fa-lg"></span> اضافة مهمة</div>',
                    enabled: function () {
                        return true
                    },
                    click: function ($itemScope, $event, value) {
                        $scope.openCreateModel();
                    }
                });
        }

        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_UPDATE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-edit fa-lg"></span> تعديل بيانات مهمة</div>',
                    enabled: function () {
                        return true
                    },
                    click: function ($itemScope, $event, value) {
                        $scope.openUpdateModel($itemScope.task);
                    }
                });
        }

        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_DELETE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف مهمة</div>',
                    enabled: function () {
                        return true
                    },
                    click: function ($itemScope, $event, value) {
                        $scope.delete($itemScope.task);
                    }
                });
        }

        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_OPERATION_CREATE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-plus fa-lg"></span> اضافة حركة</div>',
                    enabled: function () {
                        return true
                    },
                    click: function ($itemScope, $event, value) {
                        $scope.openCreateOperationModel($itemScope.task);
                    }
                });
        }

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-power-off fa-lg"></span> طلب إغلاق</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openRequestCloseModel($itemScope.task);
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-gg fa-lg"></span> الحركات / المرفقات</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.showSlideOperation();
                    $scope.setSelected($itemScope.task);
                }
            });

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);