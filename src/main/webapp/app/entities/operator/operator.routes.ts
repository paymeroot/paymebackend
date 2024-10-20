import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { OperatorComponent } from './list/operator.component';
import { OperatorDetailComponent } from './detail/operator-detail.component';
import { OperatorUpdateComponent } from './update/operator-update.component';
import OperatorResolve from './route/operator-routing-resolve.service';

const operatorRoute: Routes = [
  {
    path: '',
    component: OperatorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OperatorDetailComponent,
    resolve: {
      operator: OperatorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OperatorUpdateComponent,
    resolve: {
      operator: OperatorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OperatorUpdateComponent,
    resolve: {
      operator: OperatorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default operatorRoute;
