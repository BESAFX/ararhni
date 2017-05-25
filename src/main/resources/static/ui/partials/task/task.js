app.controller("taskCtrl", ['TaskService', 'TaskToService', 'TaskWarnService', 'TaskDeductionService', 'TaskOperationService', 'TaskCloseRequestService', 'PersonService', 'ReportModelService', 'ModalProvider', '$scope', '$rootScope', '$log', '$timeout', '$state', '$uibModal',
    function (TaskService, TaskToService, TaskWarnService, TaskDeductionService, TaskOperationService, TaskCloseRequestService, PersonService, ReportModelService, ModalProvider, $scope, $rootScope, $log, $timeout, $state, $uibModal) {

        $scope.items = [];
        $scope.items.push(
            {'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'},
            {'id': 2, 'type': 'title', 'name': 'المهام'}
        );

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
            });

            PersonService.findPersonUnderMeSummery().then(function (data) {
                $scope.persons = data;
                $scope.buffer.person = data[0];
            });

        }, 2000);

        $scope.refreshTask = function () {
            TaskService.findOne($scope.selected.id).then(function (data) {
                $scope.selected = data;
            });
        };

        $scope.refreshTaskTo = function () {
            TaskToService.findByTask($scope.selected.id).then(function (data) {
                $scope.selected.taskTos = data;
            })
        };

        $scope.openCreateTaskToModel = function () {
            ModalProvider.openTaskToCreateModel($scope.selected);
        };

        $scope.openRemoveTaskToModel = function () {
            ModalProvider.openTaskToRemoveModel($scope.selected);
        };

        $scope.openTaskToOpenDialog = function () {
            ModalProvider.openTaskToOpenModel($scope.selected);
        };

        $scope.refreshTaskCloseRequests = function () {
            var search = [];
            search.push('taskId=');
            search.push($scope.selected.id);
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.selected.taskCloseRequests = data;
            })
        };

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
            if ($scope.buffer.importance) {
                search.push('importance=');
                search.push($scope.buffer.importance);
                search.push('&');
            }
            if ($scope.buffer.closeType) {
                search.push('closeType=');
                search.push($scope.buffer.closeType);
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

                $scope.items = [];
                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                if ($scope.buffer.taskType) {
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});
                } else {
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});
                }

                switch ($scope.buffer.closeType) {
                    case 'Pending':
                        $scope.items.push({'id': 3, 'type': 'title', 'name': 'تحت التنفيذ'});
                        break;
                    case 'Auto':
                        $scope.items.push({'id': 3, 'type': 'title', 'name': 'تلقائي'});
                        break;
                    case 'Manual':
                        $scope.items.push({'id': 3, 'type': 'title', 'name': 'ارشيف'});
                        break;
                }

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                    {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                );
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
            $scope.buffer.person = $scope.persons[0];
        };

        $scope.delete = function (task) {
            if (task) {
                $rootScope.showConfirmNotify("المهام", "هل تود حذف المهمة فعلاً؟", "error", "fa-black-tie", function () {
                    TaskService.remove(task.id).then(function () {
                        $scope.filter();
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("المهام", "هل تود حذف المهمة فعلاً؟", "error", "fa-black-tie", function () {
                TaskService.remove($scope.selected.id).then(function () {
                    $scope.filter();
                });
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

        $scope.openCreateWarnModel = function (task) {
            if (task) {
                ModalProvider.openTaskWarnCreateModel(task);
                return;
            }
            ModalProvider.openTaskWarnCreateModel($scope.selected);
        };

        $scope.openWarnsModel = function (task) {
            if (task) {
                ModalProvider.openTaskWarnsModel(task);
                return;
            }
            ModalProvider.openTaskWarnsModel($scope.selected);
        };

        $scope.openCreateDeductionModel = function (task) {
            if (task) {
                ModalProvider.openTaskDeductionCreateModel(task);
                return;
            }
            ModalProvider.openTaskDeductionCreateModel($scope.selected);
        };

        $scope.openDeductionsModel = function (task) {
            if (task) {
                ModalProvider.openTaskDeductionsModel(task);
                return;
            }
            ModalProvider.openTaskDeductionsModel($scope.selected);
        };

        $scope.openTaskOperationsReportModel = function () {
            ModalProvider.openTaskOperationsReportModel($scope.tasks);
        };

        $scope.openIncomingTasksDeductionsReportModel = function () {
            ModalProvider.openIncomingTasksDeductionsModel();
        };

        $scope.openOutgoingTasksDeductionsReportModel = function () {
            ModalProvider.openOutgoingTasksDeductionsModel();
        };

        $scope.openTasksClosedSoonReportModel = function () {
            ModalProvider.openTasksClosedSoonModel();
        };

        $scope.openTaskTosReportModel = function () {
            ModalProvider.openTaskTosReportModel($scope.tasks);
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
                ModalProvider.openTaskRequestCloseModel(task, true);
                return;
            }
            ModalProvider.openTaskRequestCloseModel($scope.selected, true);
        };

        $scope.openRequestExtensionModel = function (task) {
            if (task) {
                ModalProvider.openTaskRequestCloseModel(task, false);
                return;
            }
            ModalProvider.openTaskRequestCloseModel($scope.selected, false);
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

        $scope.openExtensionModel = function (task) {
            if (task) {
                ModalProvider.openTaskExtensionModel(task);
                return;
            }
            ModalProvider.openTaskExtensionModel($scope.selected);
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

        $scope.closeTaskCompletely = function (task) {
            if (task) {
                $rootScope.showConfirmNotify("المهام", "هل تود إغلاق المهمة نهائياً ونقلها إلى الارشيف فعلاً؟", "error", "fa-black-tie", function () {
                    TaskService.closeTaskCompletely(task.id).then(function (data) {
                        $scope.filter();
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("المهام", "هل تود إغلاق المهمة نهائياً ونقلها إلى الارشيف فعلاً؟", "error", "fa-black-tie", function () {
                TaskService.closeTaskCompletely($scope.selected.id).then(function (data) {
                    $scope.filter();
                });
            });
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
        if ($rootScope.contains($rootScope.authorities, ['ROLE_TASK_UPDATE'])) {
            $scope.rowMenu.push(
                {
                    html: '<div style="cursor: pointer;padding: 10px;text-align: right"> تمديد مهمة <span class="fa fa-battery fa-lg"></span></div>',
                    enabled: function () {
                        return true
                    },
                    click: function ($itemScope, $event, value) {
                        $scope.openExtensionModel($itemScope.task);
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
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> طلب تمديد <span class="fa fa-battery fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openRequestExtensionModel($itemScope.task);
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
                html: '<div style="cursor: pointer;padding: 10px;text-align: right">لوحة التحكم <span class="fa fa-gears fa-lg"></span></div>',
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


        $scope.openFetchIncomingPending = function () {
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
                        return 'المهام الواردة / تحت التنفيذ';
                    },
                    taskType: function () {
                        return true;
                    },
                    closeType: function () {
                        return 'Pending';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الواردة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Pending');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الواردة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];

                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'تحت التنفيذ'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );

                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchIncomingAuto = function () {
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
                        return 'المهام الواردة / المغلقة تلقائي';
                    },
                    taskType: function () {
                        return true;
                    },
                    closeType: function () {
                        return 'Auto';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الواردة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Auto');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الواردة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];

                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'تلقائي'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );

                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchIncomingManual = function () {
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
                        return 'المهام الواردة / الارشيف';
                    },
                    taskType: function () {
                        return true;
                    },
                    closeType: function () {
                        return 'Manual';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الواردة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Manual');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الواردة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];

                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'ارشيف'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );

                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.fetchIncomingTasksPending = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الواردة تحت التنفيذ، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Pending');
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الواردة تحت التنفيذ بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'تحت التنفيذ'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.fetchIncomingTasksAuto = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الواردة المغلقة تلقائي، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Auto');
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الواردة المغلقة تلقائي بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'تلقائي'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.fetchIncomingTasksManual = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الواردة فى الارشيف، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Manual');
            search.push('&');
            search.push('taskType=');
            search.push(true);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الواردة فى الارشيف بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الواردة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'ارشيف'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.openFetchOutgoingPending = function () {
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
                        return 'المهام الصادرة / تحت التنفيذ';
                    },
                    taskType: function () {
                        return false;
                    },
                    closeType: function () {
                        return 'Pending';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الصادرة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Pending');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الصادرة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'تحت التنفيذ'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchOutgoingAuto = function () {
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
                        return 'المهام الصادرة / المغلقة تلقائي';
                    },
                    taskType: function () {
                        return false;
                    },
                    closeType: function () {
                        return 'Auto';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الصادرة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Auto');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الصادرة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'تلقائي'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openFetchOutgoingManual = function () {
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
                        return 'المهام الصادرة / الارشيف';
                    },
                    taskType: function () {
                        return false;
                    },
                    closeType: function () {
                        return 'Manual';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                $rootScope.showNotify("ادارة المهام", "جاري تحميل جميع المهام الصادرة، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
                var search = [];

                if (buffer.title) {
                    search.push('title=');
                    search.push(buffer.title);
                    search.push('&');
                }
                if (buffer.importance) {
                    search.push('importance=');
                    search.push(buffer.importance);
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

                search.push('closeType=');
                search.push('Manual');
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
                    $rootScope.showNotify("ادارة المهام", "تم تحميل جميع المهام الصادرة بنجاح", "success", "fa-black-tie");

                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'ارشيف'});

                    $scope.items.push(
                        {'id': 4, 'type': 'title', 'name': $scope.buffer.person.nickname},
                        {'id': 5, 'type': 'title', 'name': $scope.buffer.person.name}
                    );
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.fetchOutgoingTasksPending = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الصادرة تحت التنفيذ، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Pending');
            search.push('&');
            search.push('taskType=');
            search.push(false);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الصادرة تحت التنفيذ بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'تحت التنفيذ'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.fetchOutgoingTasksAuto = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الواردة المغلقة تلقائي، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Auto');
            search.push('&');
            search.push('taskType=');
            search.push(false);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الصادرة المغلقة تلقائي بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'تلقائي'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.fetchOutgoingTasksManual = function () {

            $rootScope.showNotify("ادارة المهام", "جاري تحميل المهام الصادرة فى الارشيف، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var search = [];

            search.push('closeType=');
            search.push('Manual');
            search.push('&');
            search.push('taskType=');
            search.push(false);
            search.push('&');
            search.push('person=');
            search.push($rootScope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskService.filter(search.join("")).then(function (data) {
                $scope.tasks = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("ادارة المهام", "تم تحميل المهام الصادرة فى الارشيف بنجاح", "success", "fa-black-tie");

                $scope.items = [];

                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});

                $scope.items.push({'id': 2, 'type': 'title', 'name': 'المهام الصادرة'});

                $scope.items.push({'id': 3, 'type': 'title', 'name': 'ارشيف'});

                $scope.items.push(
                    {'id': 4, 'type': 'title', 'name': $rootScope.me.nickname},
                    {'id': 5, 'type': 'title', 'name': $rootScope.me.name}
                );

            });
        };

        $scope.deleteTaskCloseRequests = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع طلبات الإغلاق فعلاً؟", "error", "fa-trash", function () {
                TaskCloseRequestService.deleteByTaskAndType($scope.selected.id, true).then(function (data) {
                    $scope.refreshTaskCloseRequests();
                });
            });
        };

        $scope.deleteTaskExtendRequests = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع طلبات التمديد فعلاً؟", "error", "fa-trash", function () {
                TaskCloseRequestService.deleteByTaskAndType($scope.selected.id, false).then(function (data) {
                    $scope.refreshTaskCloseRequests();
                });
            });
        };

        $scope.clearAllWarns = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع التحذيرات فعلاً؟", "error", "fa-trash", function () {
                TaskWarnService.clearAllCounters($scope.selected.id).then(function (data) {
                    $scope.findTaskWarns();
                });
            });
        };

        $scope.clearAllDeductions = function () {
            $rootScope.showConfirmNotify("المهام", "هل تود حذف جميع الخصومات فعلاً؟", "error", "fa-trash", function () {
                TaskDeductionService.clearAllCounters($scope.selected.id).then(function (data) {
                    $scope.findTaskDeductions();
                });
            });
        };

        $scope.findTaskWarns = function () {
            TaskWarnService.findByTask($scope.selected).then(function (data) {
                $scope.selected.taskWarns = data;
            });
        };

        $scope.findTaskDeductions = function () {
            TaskDeductionService.findByTask($scope.selected).then(function (data) {
                $scope.selected.taskDeductions = data;
            })
        };

        $scope.openClearWarnsAndDeductionsModel = function () {
            ModalProvider.openClearCountersModel($scope.selected);
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);