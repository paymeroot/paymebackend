import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOperator } from '../operator.model';
import { OperatorService } from '../service/operator.service';

const operatorResolve = (route: ActivatedRouteSnapshot): Observable<null | IOperator> => {
  const id = route.params['id'];
  if (id) {
    return inject(OperatorService)
      .find(id)
      .pipe(
        mergeMap((operator: HttpResponse<IOperator>) => {
          if (operator.body) {
            return of(operator.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default operatorResolve;
