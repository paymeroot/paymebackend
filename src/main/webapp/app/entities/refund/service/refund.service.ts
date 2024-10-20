import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRefund, NewRefund } from '../refund.model';

export type PartialUpdateRefund = Partial<IRefund> & Pick<IRefund, 'id'>;

type RestOf<T extends IRefund | NewRefund> = Omit<T, 'refundDate'> & {
  refundDate?: string | null;
};

export type RestRefund = RestOf<IRefund>;

export type NewRestRefund = RestOf<NewRefund>;

export type PartialUpdateRestRefund = RestOf<PartialUpdateRefund>;

export type EntityResponseType = HttpResponse<IRefund>;
export type EntityArrayResponseType = HttpResponse<IRefund[]>;

@Injectable({ providedIn: 'root' })
export class RefundService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/refunds');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/refunds/_search');

  create(refund: NewRefund): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(refund);
    return this.http
      .post<RestRefund>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(refund: IRefund): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(refund);
    return this.http
      .put<RestRefund>(`${this.resourceUrl}/${this.getRefundIdentifier(refund)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(refund: PartialUpdateRefund): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(refund);
    return this.http
      .patch<RestRefund>(`${this.resourceUrl}/${this.getRefundIdentifier(refund)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRefund>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRefund[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestRefund[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IRefund[]>()], asapScheduler)),
    );
  }

  getRefundIdentifier(refund: Pick<IRefund, 'id'>): number {
    return refund.id;
  }

  compareRefund(o1: Pick<IRefund, 'id'> | null, o2: Pick<IRefund, 'id'> | null): boolean {
    return o1 && o2 ? this.getRefundIdentifier(o1) === this.getRefundIdentifier(o2) : o1 === o2;
  }

  addRefundToCollectionIfMissing<Type extends Pick<IRefund, 'id'>>(
    refundCollection: Type[],
    ...refundsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const refunds: Type[] = refundsToCheck.filter(isPresent);
    if (refunds.length > 0) {
      const refundCollectionIdentifiers = refundCollection.map(refundItem => this.getRefundIdentifier(refundItem));
      const refundsToAdd = refunds.filter(refundItem => {
        const refundIdentifier = this.getRefundIdentifier(refundItem);
        if (refundCollectionIdentifiers.includes(refundIdentifier)) {
          return false;
        }
        refundCollectionIdentifiers.push(refundIdentifier);
        return true;
      });
      return [...refundsToAdd, ...refundCollection];
    }
    return refundCollection;
  }

  protected convertDateFromClient<T extends IRefund | NewRefund | PartialUpdateRefund>(refund: T): RestOf<T> {
    return {
      ...refund,
      refundDate: refund.refundDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRefund: RestRefund): IRefund {
    return {
      ...restRefund,
      refundDate: restRefund.refundDate ? dayjs(restRefund.refundDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRefund>): HttpResponse<IRefund> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRefund[]>): HttpResponse<IRefund[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
