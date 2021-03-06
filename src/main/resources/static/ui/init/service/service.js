app.service('ModalProvider', ['$uibModal', '$log', function ($uibModal, $log) {
    /**************************************************************
     *                                                            *
     * Company Model                                              *
     *                                                            *
     *************************************************************/
    this.openCompanyCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/company/companyCreateUpdate.html',
            controller: 'companyCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'اضافة شركة جديدة';
                },
                action: function () {
                    return 'create';
                },
                company: function () {
                    return undefined;
                }
            }
        });
    };

    this.openCompanyUpdateModel = function (company) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/company/companyCreateUpdate.html',
            controller: 'companyCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات شركة';
                },
                action: function () {
                    return 'update';
                },
                company: function () {
                    return company;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Region Model                                               *
     *                                                            *
     *************************************************************/
    this.openRegionCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/region/regionCreateUpdate.html',
            controller: 'regionCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'اضافة منطقة جديدة';
                },
                action: function () {
                    return 'create';
                },
                region: function () {
                    return undefined;
                }
            }
        });
    };

    this.openRegionUpdateModel = function (region) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/region/regionCreateUpdate.html',
            controller: 'regionCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات منطقة';
                },
                action: function () {
                    return 'update';
                },
                region: function () {
                    return region;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Branch Model                                               *
     *                                                            *
     *************************************************************/
    this.openBranchCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/branch/branchCreateUpdate.html',
            controller: 'branchCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'اضافة فرع جديد';
                },
                action: function () {
                    return 'create';
                },
                branch: function () {
                    return undefined;
                }
            }
        });
    };

    this.openBranchUpdateModel = function (branch) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/branch/branchCreateUpdate.html',
            controller: 'branchCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات فرع';
                },
                action: function () {
                    return 'update';
                },
                branch: function () {
                    return branch;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Department Model                                           *
     *                                                            *
     *************************************************************/
    this.openDepartmentCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/department/departmentCreateUpdate.html',
            controller: 'departmentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'اضافة قسم جديد';
                },
                action: function () {
                    return 'create';
                },
                department: function () {
                    return undefined;
                }
            }
        });
    };

    this.openDepartmentUpdateModel = function (department) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/department/departmentCreateUpdate.html',
            controller: 'departmentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات قسم';
                },
                action: function () {
                    return 'update';
                },
                department: function () {
                    return department;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Employee Model                                             *
     *                                                            *
     *************************************************************/
    this.openEmployeeCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/employee/employeeCreateUpdate.html',
            controller: 'employeeCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'اضافة موظف جديد';
                },
                action: function () {
                    return 'create';
                },
                employee: function () {
                    return undefined;
                }
            }
        });
    };

    this.openEmployeeUpdateModel = function (employee) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/employee/employeeCreateUpdate.html',
            controller: 'employeeCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات موظف';
                },
                action: function () {
                    return 'update';
                },
                employee: function () {
                    return employee;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Person Model                                               *
     *                                                            *
     *************************************************************/
    this.openPersonCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'اضافة مستخدم جديد';
                },
                action: function () {
                    return 'create';
                },
                person: function () {
                    return undefined;
                }
            }
        });
    };

    this.openPersonUpdateModel = function (person) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات مستخدم';
                },
                action: function () {
                    return 'update';
                },
                person: function () {
                    return person;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Team Model                                                 *
     *                                                            *
     *************************************************************/
    this.openTeamCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'اضافة مجموعة جديدة';
                },
                action: function () {
                    return 'create';
                },
                team: function () {
                    return undefined;
                }
            }
        });
    };

    this.openTeamUpdateModel = function (team) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات مجموعة';
                },
                action: function () {
                    return 'update';
                },
                team: function () {
                    return team;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Task Model                                                 *
     *                                                            *
     *************************************************************/
    this.openTaskCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskCreateUpdate.html',
            controller: 'taskCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'اضافة مهمة جديد';
                },
                action: function () {
                    return 'create';
                },
                task: function () {
                    return undefined;
                }
            }
        });
    };

    this.openTaskUpdateModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskCreateUpdate.html',
            controller: 'taskCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات مهمة';
                },
                action: function () {
                    return 'update';
                },
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskDetailsModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskDetails.html',
            controller: 'taskDetailsCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskWarnsModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskWarns.html',
            controller: 'taskWarnsCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskDeductionsModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskDeductions.html',
            controller: 'taskDeductionsCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskOperationsReportModel = function (tasks) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/taskOperationsIn.html',
            controller: 'taskOperationsInCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                tasks: function () {
                    return tasks;
                }
            }
        });
    };

    this.openTaskTosReportModel = function (tasks) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/taskTosIn.html',
            controller: 'taskTosInCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                tasks: function () {
                    return tasks;
                }
            }
        });
    };

    this.openTasksReportModel = function (tasks) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/tasksIn.html',
            controller: 'tasksInCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                tasks: function () {
                    return tasks;
                }
            }
        });
    };

    this.openPersonsReportModel = function (persons) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/person/personsIn.html',
            controller: 'personsInCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                persons: function () {
                    return persons;
                }
            }
        });
    };

    this.openIncomingTasksDeductionsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/incomingTasksDeductions.html',
            controller: 'incomingTasksDeductionsCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    this.openOutgoingTasksDeductionsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/outgoingTasksDeductions.html',
            controller: 'outgoingTasksDeductionsCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    this.openTasksClosedSoonModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/tasksClosedSoon.html',
            controller: 'tasksClosedSoonCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    this.openOutgoingTasksOperationsTodayModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/outgoingTasksOperationsToday.html',
            controller: 'outgoingTasksOperationsTodayCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    this.openWatchTasksOperationsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/report/task/watchTasksOperations.html',
            controller: 'watchTasksOperationsCtrl',
            backdrop: 'static',
            keyboard: false
        });
    };

    /**************************************************************
     *                                                            *
     * Task Operation Model                                       *
     *                                                            *
     *************************************************************/
    this.openTaskOperationModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskOperation.html',
            controller: 'taskOperationCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskOperationCreateModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskOperationCreate.html',
            controller: 'taskOperationCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskRequestCloseModel = function (task, type) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskRequestClose.html',
            controller: 'taskRequestCloseCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                },
                type: function () {
                    return type;
                },
                title: function () {
                    return type ? 'طلب إغلاق مهمة' : 'طلب تمديد مهمة';
                }
            }
        });
    };

    this.openTaskProgressModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskProgress.html',
            controller: 'taskProgressCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskClosedModel = function (task) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskClosed.html',
            controller: 'taskClosedCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskExtensionModel = function (task) {
        return $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskExtension.html',
            controller: 'taskExtensionCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskWarnCreateModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskWarnCreate.html',
            controller: 'taskWarnCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskDeductionCreateModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskDeductionCreate.html',
            controller: 'taskDeductionCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskToCreateModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskToCreate.html',
            controller: 'taskToCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskToRemoveModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskToRemove.html',
            controller: 'taskToRemoveCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openTaskToOpenModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/taskToOpen.html',
            controller: 'taskToOpenCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    this.openClearCountersModel = function (task) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/task/clearCounters.html',
            controller: 'clearCountersCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                task: function () {
                    return task;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * ReportModel Model                                          *
     *                                                            *
     *************************************************************/
    this.openReportModelCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/reportModel/reportModelCreateUpdate.html',
            controller: 'reportModelCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'اضافة نموذج طباعة جديد';
                },
                action: function () {
                    return 'create';
                },
                reportModel: function () {
                    return undefined;
                }
            }
        });
    };

    this.openReportModelUpdateModel = function (reportModel) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/reportModel/reportModelCreateUpdate.html',
            controller: 'reportModelCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات نموذج طباعة';
                },
                action: function () {
                    return 'update';
                },
                reportModel: function () {
                    return reportModel;
                }
            }
        });
    };

}]);

app.service('NotificationProvider', ['$http', function ($http) {

    this.notifyOne = function (code, title, message, type, receiver) {
        $http.post("/notifyOne?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
            + '&'
            + 'receiver=' + receiver);
    };
    this.notifyAll = function (code, title, message, type) {
        $http.post("/notifyAll?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };
    this.notifyAllExceptMe = function (code, title, message, type) {
        $http.post("/notifyAllExceptMe?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };

}]);

