import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { OperatorService } from '../service/operator.service';
import { IOperator } from '../operator.model';
import { OperatorFormService } from './operator-form.service';

import { OperatorUpdateComponent } from './operator-update.component';

describe('Operator Management Update Component', () => {
  let comp: OperatorUpdateComponent;
  let fixture: ComponentFixture<OperatorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let operatorFormService: OperatorFormService;
  let operatorService: OperatorService;
  let countryService: CountryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OperatorUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OperatorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OperatorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    operatorFormService = TestBed.inject(OperatorFormService);
    operatorService = TestBed.inject(OperatorService);
    countryService = TestBed.inject(CountryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call country query and add missing value', () => {
      const operator: IOperator = { id: 456 };
      const country: ICountry = { id: 12477 };
      operator.country = country;

      const countryCollection: ICountry[] = [{ id: 17937 }];
      jest.spyOn(countryService, 'query').mockReturnValue(of(new HttpResponse({ body: countryCollection })));
      const expectedCollection: ICountry[] = [country, ...countryCollection];
      jest.spyOn(countryService, 'addCountryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ operator });
      comp.ngOnInit();

      expect(countryService.query).toHaveBeenCalled();
      expect(countryService.addCountryToCollectionIfMissing).toHaveBeenCalledWith(countryCollection, country);
      expect(comp.countriesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const operator: IOperator = { id: 456 };
      const country: ICountry = { id: 20680 };
      operator.country = country;

      activatedRoute.data = of({ operator });
      comp.ngOnInit();

      expect(comp.countriesCollection).toContain(country);
      expect(comp.operator).toEqual(operator);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOperator>>();
      const operator = { id: 123 };
      jest.spyOn(operatorFormService, 'getOperator').mockReturnValue(operator);
      jest.spyOn(operatorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operator });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: operator }));
      saveSubject.complete();

      // THEN
      expect(operatorFormService.getOperator).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(operatorService.update).toHaveBeenCalledWith(expect.objectContaining(operator));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOperator>>();
      const operator = { id: 123 };
      jest.spyOn(operatorFormService, 'getOperator').mockReturnValue({ id: null });
      jest.spyOn(operatorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operator: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: operator }));
      saveSubject.complete();

      // THEN
      expect(operatorFormService.getOperator).toHaveBeenCalled();
      expect(operatorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOperator>>();
      const operator = { id: 123 };
      jest.spyOn(operatorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operator });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(operatorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCountry', () => {
      it('Should forward to countryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(countryService, 'compareCountry');
        comp.compareCountry(entity, entity2);
        expect(countryService.compareCountry).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
