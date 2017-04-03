app.controller("taskCtrl", ['TaskService', 'TaskOperationService', 'TaskCloseRequestService', 'PersonService', 'ReportModelService', 'ModalProvider', '$scope', '$rootScope', '$log', '$timeout', '$state', '$uibModal',
    function (TaskService, TaskOperationService, TaskCloseRequestService, PersonService, ReportModelService, ModalProvider, $scope, $rootScope, $log, $timeout, $state, $uibModal) {

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

            $scope.selected = {};

            $scope.selectedOperation = {};

            ReportModelService.findAll().then(function (data) {
                $scope.reportModels = data;
                $rootScope.showNotify("المهام", "تم تحميل نماذج الطباعة بنجاح", "success", "fa-black-tie");
            });

            PersonService.findPersonUnderMe().then(function (data) {
                $scope.persons = data;
                $scope.buffer.person = data[0];
            });

        }, 2000);

        $scope.setReportProp = function () {
            $scope.reportProp = JSON.parse($scope.buffer.reportModel.template);
        };

        $scope.filter = function () {
            $rootScope.showNotify("المهام", "جاري تصفية المهام، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
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
                $scope.setSelected(data[0]);
                $rootScope.showNotify("المهام", "تم التحميل بنجاح، يمكنك متابعة عملك الآن", "success", "fa-black-tie");
            });
        };

        $scope.printFilteredTasks = function () {
            $rootScope.showNotify("المهام", "جاري إعداد التقرير، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
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
            // $scope.buffer.taskType = true;
            // $scope.buffer.isTaskOpen = true;
            $scope.buffer.person = $scope.persons[0];
        };

        $scope.delete = function (task) {
            if (task) {
                $rootScope.showConfirmNotify("المهام", "هل تود حذف المهمة فعلاً؟", "error", "fa-black-tie", function () {
                    TaskService.remove(task.id);
                });
                return;
            }
            $rootScope.showConfirmNotify("المهام", "هل تود حذف المهمة فعلاً؟", "error", "fa-black-tie", function () {
                TaskService.remove($scope.selected.id);
            });
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

        $scope.openDetailsModel = function (task) {
            if (task) {
                ModalProvider.openTaskDetailsModel(task);
                return;
            }
            ModalProvider.openTaskDetailsModel($scope.selected);
        };

        $scope.openTaskOperationsReportModel = function () {
            ModalProvider.openTaskOperationsReportModel($scope.tasks);
        };

        $scope.openReportTasksModel = function () {
            ModalProvider.openTasksReportModel($scope.tasks);
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

        $scope.openProgressModel = function (task) {
            if (task) {
                ModalProvider.openTaskProgressModel(task);
                return;
            }
            ModalProvider.openTaskProgressModel($scope.selected);
        };

        $scope.openClosedModel = function (task) {
            if (task) {
                ModalProvider.openTaskClosedModel(task);
                return;
            }
            ModalProvider.openTaskClosedModel($scope.selected);
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
            $rootScope.showNotify("المهام", "جاري تحميل طلبات الإغلاق، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];
            search.push('taskId=');
            search.push($scope.selected.id);
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.selected.taskCloseRequests = data;
                $rootScope.showNotify("المهام", "تم تحميل طلبات الإغلاق بنجاح", "success", "fa-black-tie");
            })
        };

        $scope.openCreateOperationModel = function (task) {
            if (task) {
                ModalProvider.openTaskOperationCreateModel(task);
                return;
            }
            ModalProvider.openTaskOperationCreateModel($scope.selected);
        };

        $scope.rowMenu = [];

        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_CREATE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px;text-align: right"> اضافة مهمة <span class="fa fa-plus fa-lg"></span></div>',
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
                    html: '<div style="cursor: pointer;padding: 10px;text-align: right"> تعديل بيانات مهمة <span class="fa fa-edit fa-lg"></span></div>',
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
                    html: '<div style="cursor: pointer;padding: 10px;text-align: right"> حذف مهمة <span class="fa fa-minus-square-o fa-lg"></span></div>',
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
                    html: '<div style="cursor: pointer;padding: 10px;text-align: right"> اضافة حركة <span class="fa fa-plus fa-lg"></span></div>',
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
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> طلب إغلاق <span class="fa fa-power-off fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openRequestCloseModel($itemScope.task);
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> تحديد نسبة الإنجاز <span class="fa fa-hourglass-2 fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openProgressModel($itemScope.task);
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> الحركات / المرفقات <span class="fa fa-gg fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.showSlideOperation();
                    $scope.setSelected($itemScope.task);
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openDetailsModel($itemScope.task);
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> طباعة الحركات <span class="fa fa-print fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openTaskOperationsReportModel();
                }
            });

        $scope.rowMenu.push(
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> طباعة تقرير مختصر <span class="fa fa-print fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openReportTasksModel();
                }
            });

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);


        $scope.openFetchIncomingOpened = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/task/taskFilter.html',
                controller: 'taskFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'المهام الواردة السارية';
                    },
                    taskType: function () {
                        return true;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الواردة السارية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.startDateTo) {
                    search.push('startDateTo=');
                    search.push(buffer.startDateTo.getTime());
                    search.push('&');
                }
                if (buffer.startDateFrom) {
                    search.push('startDateFrom=');
                    search.push(buffer.startDateFrom.getTime());
                    search.push('&');
                }
                if (buffer.endDateTo) {
                    search.push('endDateTo=');
                    search.push(buffer.endDateTo.getTime());
                    search.push('&');
                }
                if (buffer.endDateFrom) {
                    search.push('endDateFrom=');
                    search.push(buffer.endDateFrom.getTime());
                    search.push('&');
                }

                search.push('isTaskOpen=');
                search.push(true);
                search.push('&');
                search.push('taskType=');
                search.push(true);
                search.push('&');
                search.push('person=');
                search.push(buffer.person.id);
                search.push('&');
                search.push('timeType=');
                search.push('All');
                search.push('&');
                TaskService.filter(search.join("")).then(function (data) {
                    $scope.tasks = data;
                    $scope.setSelected(data[0]);
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الواردة السارية بنجاح", "success", "fa-black-tie");
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchIncomingClosed = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/task/taskFilter.html',
                controller: 'taskFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'المهام الواردة المغلقة';
                    },
                    taskType: function () {
                        return true;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الواردة المغلقة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.startDateTo) {
                    search.push('startDateTo=');
                    search.push(buffer.startDateTo.getTime());
                    search.push('&');
                }
                if (buffer.startDateFrom) {
                    search.push('startDateFrom=');
                    search.push(buffer.startDateFrom.getTime());
                    search.push('&');
                }
                if (buffer.endDateTo) {
                    search.push('endDateTo=');
                    search.push(buffer.endDateTo.getTime());
                    search.push('&');
                }
                if (buffer.endDateFrom) {
                    search.push('endDateFrom=');
                    search.push(buffer.endDateFrom.getTime());
                    search.push('&');
                }

                search.push('isTaskOpen=');
                search.push(false);
                search.push('&');
                search.push('taskType=');
                search.push(true);
                search.push('&');
                search.push('person=');
                search.push(buffer.person.id);
                search.push('&');
                search.push('timeType=');
                search.push('All');
                search.push('&');
                TaskService.filter(search.join("")).then(function (data) {
                    $scope.tasks = data;
                    $scope.setSelected(data[0]);
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الواردة المغلقة بنجاح", "success", "fa-black-tie");
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchOutgoingOpened = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/task/taskFilter.html',
                controller: 'taskFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'المهام الصادرة السارية';
                    },
                    taskType: function () {
                        return false;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الصادرة السارية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.startDateTo) {
                    search.push('startDateTo=');
                    search.push(buffer.startDateTo.getTime());
                    search.push('&');
                }
                if (buffer.startDateFrom) {
                    search.push('startDateFrom=');
                    search.push(buffer.startDateFrom.getTime());
                    search.push('&');
                }
                if (buffer.endDateTo) {
                    search.push('endDateTo=');
                    search.push(buffer.endDateTo.getTime());
                    search.push('&');
                }
                if (buffer.endDateFrom) {
                    search.push('endDateFrom=');
                    search.push(buffer.endDateFrom.getTime());
                    search.push('&');
                }

                search.push('isTaskOpen=');
                search.push(true);
                search.push('&');
                search.push('taskType=');
                search.push(false);
                search.push('&');
                search.push('person=');
                search.push(buffer.person.id);
                search.push('&');
                search.push('timeType=');
                search.push('All');
                search.push('&');
                TaskService.filter(search.join("")).then(function (data) {
                    $scope.tasks = data;
                    $scope.setSelected(data[0]);
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الصادرة السارية بنجاح", "success", "fa-black-tie");
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchOutgoingClosed = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/task/taskFilter.html',
                controller: 'taskFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'المهام الصادرة المغلقة';
                    },
                    taskType: function () {
                        return false;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الصادرة المغلقة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.startDateTo) {
                    search.push('startDateTo=');
                    search.push(buffer.startDateTo.getTime());
                    search.push('&');
                }
                if (buffer.startDateFrom) {
                    search.push('startDateFrom=');
                    search.push(buffer.startDateFrom.getTime());
                    search.push('&');
                }
                if (buffer.endDateTo) {
                    search.push('endDateTo=');
                    search.push(buffer.endDateTo.getTime());
                    search.push('&');
                }
                if (buffer.endDateFrom) {
                    search.push('endDateFrom=');
                    search.push(buffer.endDateFrom.getTime());
                    search.push('&');
                }

                search.push('isTaskOpen=');
                search.push(false);
                search.push('&');
                search.push('taskType=');
                search.push(false);
                search.push('&');
                search.push('person=');
                search.push(buffer.person.id);
                search.push('&');
                search.push('timeType=');
                search.push('All');
                search.push('&');
                TaskService.filter(search.join("")).then(function (data) {
                    $scope.tasks = data;
                    $scope.setSelected(data[0]);
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الصادرة المغلقة بنجاح", "success", "fa-black-tie");
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

    }]);