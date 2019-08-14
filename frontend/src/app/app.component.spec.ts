import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TestBed, async } from '@angular/core/testing';
import { MatTableModule } from '@angular/material';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { WikiSearchModel } from './wiki-search.model';
import { WikiSearchService } from './wiki-search.service';

describe('AppComponent', () => {
  beforeEach(async(() => {
    const wikiSearchServiceStub = {
      getWikiArticles: () => ({
        subscribe: () => ({})
      })
    };

    TestBed.configureTestingModule({
      imports: [
        MatTableModule
      ],
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: WikiSearchService, useValue: wikiSearchServiceStub }
      ]
    }).compileComponents();

  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'AID Wikipedia Search'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('AID Wikipedia Search');
  });

  it('should render title in a h1 tag', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Search Results');
  });

  it('should get exactly one result', () => {

    const wikiSearchModelList = [];
    const wikiSearchModel: WikiSearchModel = {
      id: '45',
      contributor: 'Frank',
      title: 'Frankenstein'
    };

    wikiSearchModelList.push(wikiSearchModel);

    const fixture = TestBed.createComponent(AppComponent);
    const comp = fixture.componentInstance;
    const wikiSearchStub: WikiSearchService = fixture.debugElement.injector.get(WikiSearchService);
    spyOn(wikiSearchStub, 'getWikiArticles').and.returnValue(of(wikiSearchModelList));
    fixture.detectChanges();

    expect(comp.wikiSearchList).toBeDefined();
  });
});
