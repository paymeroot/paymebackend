import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICustomer, NewCustomer } from '../customer.model';

export type PartialUpdateCustomer = Partial<ICustomer> & Pick<ICustomer, 'id'>;

export type EntityResponseType = HttpResponse<ICustomer>;
export type EntityArrayResponseType = HttpResponse<ICustomer[]>;

@Injectable({ providedIn: 'root' })
export class CustomerService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customers');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/customers/_search');

  create(customer: NewCustomer): Observable<EntityResponseType> {
    return this.http.post<ICustomer>(this.resourceUrl, customer, { observe: 'response' });
  }

  update(customer: ICustomer): Observable<EntityResponseType> {
    return this.http.put<ICustomer>(`${this.resourceUrl}/${this.getCustomerIdentifier(customer)}`, customer, { observe: 'response' });
  }

  partialUpdate(customer: PartialUpdateCustomer): Observable<EntityResponseType> {
    return this.http.patch<ICustomer>(`${this.resourceUrl}/${this.getCustomerIdentifier(customer)}`, customer, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICustomer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICustomer[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICustomer[]>()], asapScheduler)));
  }

  getCustomerIdentifier(customer: Pick<ICustomer, 'id'>): number {
    return customer.id;
  }

  compareCustomer(o1: Pick<ICustomer, 'id'> | null, o2: Pick<ICustomer, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerIdentifier(o1) === this.getCustomerIdentifier(o2) : o1 === o2;
  }

  addCustomerToCollectionIfMissing<Type extends Pick<ICustomer, 'id'>>(
    customerCollection: Type[],
    ...customersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customers: Type[] = customersToCheck.filter(isPresent);
    if (customers.length > 0) {
      const customerCollectionIdentifiers = customerCollection.map(customerItem => this.getCustomerIdentifier(customerItem));
      const customersToAdd = customers.filter(customerItem => {
        const customerIdentifier = this.getCustomerIdentifier(customerItem);
        if (customerCollectionIdentifiers.includes(customerIdentifier)) {
          return false;
        }
        customerCollectionIdentifiers.push(customerIdentifier);
        return true;
      });
      return [...customersToAdd, ...customerCollection];
    }
    return customerCollection;
  }
}
