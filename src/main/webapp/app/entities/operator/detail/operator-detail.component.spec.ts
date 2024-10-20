import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { OperatorDetailComponent } from './operator-detail.component';

describe('Operator Management Detail Component', () => {
  let comp: OperatorDetailComponent;
  let fixture: ComponentFixture<OperatorDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: OperatorDetailComponent,
              resolve: { operator: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(OperatorDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load operator on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OperatorDetailComponent);

      // THEN
      expect(instance.operator()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
